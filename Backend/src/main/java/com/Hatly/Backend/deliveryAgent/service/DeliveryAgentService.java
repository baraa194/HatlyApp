package com.Hatly.Backend.deliveryAgent.service;

import com.Hatly.Backend.deliveryAgent.dto.*;
import com.Hatly.Backend.deliveryAgent.enums.AgentStatus;
import com.Hatly.Backend.deliveryAgent.mapper.DeliveryAgentMapper;
import com.Hatly.Backend.deliveryAgent.model.AgentPresence;
import com.Hatly.Backend.deliveryAgent.model.DeliveryAgent;
import com.Hatly.Backend.deliveryAgent.repo.AgentPresenceRepo;
import com.Hatly.Backend.deliveryAgent.repo.DeliveryAgentRepo;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DeliveryAgentService {
    @Autowired
    private DeliveryAgentRepo deliveryAgentRepo;
    @Autowired
    AgentPresenceRepo agentPresenceRepo;
    @Autowired
    OrderRepo orderrepo;
    @Autowired
    private AgentNotificationService agentNotificationService;
    @Autowired
    private DeliveryAgentMapper deliveryAgentMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String AGENTS_LOCATIONS_KEY = "ACTIVE_AGENTS_LOCATIONS";
    public UpdateAgentStatusResponse updateAgentStatus(Long id, Boolean IsOnline) {
      DeliveryAgent deliveryAgent = deliveryAgentRepo.findByUserId(id)
              .orElseThrow(() -> new RuntimeException( "DeliveryAgent Not Found"));

      deliveryAgent.setIsOnline(IsOnline);
      if(IsOnline){
          if (deliveryAgent.getStatus() != AgentStatus.BUSY) {
              deliveryAgent.setStatus(AgentStatus.AVAILABLE);
          }

      }
      else{
          deliveryAgent.setStatus(AgentStatus.OFFLINE);
      }
      deliveryAgentRepo.save(deliveryAgent);
        UpdateAgentStatusResponse  response = new UpdateAgentStatusResponse();
        response.setStatus(deliveryAgent.getStatus());
        response.setIsOnline(IsOnline);
        return response;

    }

    public AgentInfoResponse getAgentInfoByUserId(Long userId) {
        DeliveryAgent agent = deliveryAgentRepo.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Delivery Agent not found for user id: " + userId));

        return deliveryAgentMapper.toResponse(agent);
    }

    @Transactional
    public void updateAgentLocation(Long agentId, UpdateLocationRequest request) {

        Point point = new Point(request.getLastLng().doubleValue(), request.getLastLat().doubleValue());
        DeliveryAgent agent = deliveryAgentRepo.findByUserId(agentId)
                .orElseThrow(() -> new RuntimeException("Delivery Agent Not Found"));


        AgentPresence presence = agentPresenceRepo.findByAgentId(agent.getId())
                .orElse(new AgentPresence());


        if (presence.getId() == null) {
            presence.setAgent(agent);
        }

        presence.setLastLat(request.getLastLat());
        presence.setLastLng(request.getLastLng());
        presence.setLastSeenAt(LocalDateTime.now());

        redisTemplate.opsForGeo().add(AGENTS_LOCATIONS_KEY, point, agentId.toString());
        agentPresenceRepo.save(presence);
    }

   @Transactional
    public void updateDeliveryStatus(Long orderId, Long agentId, UpdateDeliveryStatusRequest request) {

        Order order = orderrepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        if (order.getDeliveryAgent() == null || !order.getDeliveryAgent().getUser().getId().equals(agentId)) {
            throw new RuntimeException("Unauthorized! This order is not assigned to this agent.");
        }

        String newStatus = request.getStatus().toUpperCase();

        if ("OUT_FOR_DELIVERY".equals(newStatus)) {
            order.setStatus(OrderStatus.OUT_FOR_DELIVERY);
            orderrepo.save(order);
            System.out.println("Order #" + orderId + " is PICKED_UP by Agent #" + agentId);

        } else if ("DELIVERED".equals(newStatus)) {

            order.setStatus(OrderStatus.DELIVERED);
            orderrepo.save(order);


            DeliveryAgent agent = order.getDeliveryAgent();
            agent.setStatus(AgentStatus.AVAILABLE);
            deliveryAgentRepo.save(agent);


           // BigDecimal deliveryEarnings = order.getTotal().multiply(new BigDecimal("0.10")); // 10% أرباح



        } else {
            throw new IllegalArgumentException("Invalid status! Only PICKED_UP or DELIVERED are allowed.");
        }
    }
    public List<DeliveryAgent> findNearbyAvailableAgentsFromRedis( BigDecimal resLat, BigDecimal resLng, double radiusInKm) {

        double latDouble = resLat.doubleValue();
        double lngDouble = resLng.doubleValue();
        Circle circle = new Circle(
                new Point(lngDouble, latDouble),
                new Distance(radiusInKm, Metrics.KILOMETERS)
        );


        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius("ACTIVE_AGENTS_LOCATIONS", circle);

        List<Long> nearbyAgentIds = new ArrayList<>();
        if (results != null) {
            for (GeoResult<RedisGeoCommands.GeoLocation<String>> result : results) {
                nearbyAgentIds.add(Long.parseLong(result.getContent().getName()));
            }
        }

        if (nearbyAgentIds.isEmpty()) {
            return Collections.emptyList();
        }


        return deliveryAgentRepo.findAllByUserIdInAndStatusAndIsOnlineTrue(nearbyAgentIds, "AVAILABLE");
    }
    public void processOrderAssignment(Order order) {


        BigDecimal restaurantBranchLat = order.getBranch().getLat();
        BigDecimal restaurantBranchLng = order.getBranch().getLng();


        List<DeliveryAgent> nearbyAgents = findNearbyAvailableAgentsFromRedis(restaurantBranchLat, restaurantBranchLng, 5.0);


        for (DeliveryAgent agent : nearbyAgents) {
            agentNotificationService.sendOrderOffer(agent.getUser().getId(), order);
        }

        if (nearbyAgents.isEmpty()) {
            System.out.println("No nearby available agents found for Order #" + order.getId());
        }
        System.out.println(">>> Restaurant Lat: " + restaurantBranchLat + ", Lng: " + restaurantBranchLng);
        System.out.println(">>> Number of nearby agents found: " + nearbyAgents.size());
    }
    @Transactional
    public void updateOrderStatusToReady(Long orderId) {

        Order order = orderrepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));


        order.setStatus(OrderStatus.READY_FOR_PICKUP);
        orderrepo.save(order);
        processOrderAssignment(order);
    }
    @Transactional
    public void handleOrderAction(Long orderId, Long agentId, AgentOrderActionRequest request) {

        Order order = orderrepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        DeliveryAgent agent = deliveryAgentRepo.findByUserId(agentId)
                .orElseThrow(() -> new RuntimeException("Delivery Agent Not Found"));

        if ("ACCEPT".equalsIgnoreCase(request.getAction())) {


            if (order.getDeliveryAgent() != null) {
                throw new RuntimeException("Order has already been accepted by another agent!");
            }


            order.setDeliveryAgent(agent);
            order.setStatus(OrderStatus.READY_FOR_PICKUP);
            orderrepo.save(order);


            agent.setStatus(AgentStatus.BUSY);
            deliveryAgentRepo.save(agent);

            System.out.println("Order #" + orderId + " successfully ASSIGNED to Agent #" + agentId);

        } else if ("REJECT".equalsIgnoreCase(request.getAction())) {

            System.out.println("Agent #" + agentId + " REJECTED Order #" + orderId);
        } else {
            throw new IllegalArgumentException("Invalid action! Only ACCEPT or REJECT are allowed.");
        }
    }



}
