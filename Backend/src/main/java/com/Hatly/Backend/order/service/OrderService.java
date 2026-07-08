package com.Hatly.Backend.order.service;

import com.Hatly.Backend.deliveryAgent.dto.AgentOrderActionRequest;
import com.Hatly.Backend.deliveryAgent.enums.AgentStatus;
import com.Hatly.Backend.deliveryAgent.model.DeliveryAgent;
import com.Hatly.Backend.deliveryAgent.repo.AgentPresenceRepo;
import com.Hatly.Backend.deliveryAgent.repo.DeliveryAgentRepo;
import com.Hatly.Backend.deliveryAgent.service.AgentNotificationService;
import com.Hatly.Backend.deliveryAgent.service.DeliveryAgentService;
import com.Hatly.Backend.order.dto.OrderDeliveryResponse;
import com.Hatly.Backend.order.dto.OrderRequest;
import com.Hatly.Backend.order.dto.OrderResponse;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.mapper.OrderMapper;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.model.OrderItem;
import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.payment.enums.PaymentStatus;
import com.Hatly.Backend.payment.model.PaymentProvider;
import com.Hatly.Backend.payment.repo.PaymentProviderRepo;
import com.Hatly.Backend.product.mapper.ProductResponseMapper;
import com.Hatly.Backend.product.model.ProductBranchDetail;
import com.Hatly.Backend.product.repo.ProductBranchRepo;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import com.Hatly.Backend.resturant.repo.RestBranchRepo;
import com.Hatly.Backend.resturant.repo.RestaurantRepo;
import com.Hatly.Backend.user.model.CustomerAddress;
import com.Hatly.Backend.user.model.User;
import com.Hatly.Backend.user.repo.CustomerAddresseRepo;
import com.Hatly.Backend.user.repo.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class OrderService {

    final OrderRepo orderrepo;
    final RestBranchRepo restbranchrepo;
    final CustomerAddresseRepo customeraddressrepo;
    final UserRepo userrepo;
    final ProductBranchRepo productBranchDetailRepository;
    final OrderMapper orderMapper;
    final PaymentProviderRepo providerrepo;
    private final RestaurantRepo restaurantRepo;
    private final DeliveryAgentService deliveryAgentService;
    private final AgentNotificationService agentNotificationService;


    public OrderResponse createOrder(OrderRequest request, Long userId) {

        RestaurantBranch branch = restbranchrepo.findById(request.getRestaurnat_branch_id())
                .orElseThrow(() -> new RuntimeException("the branch id is not found"));

        CustomerAddress address = customeraddressrepo.findById(request.getCustomerAddress_id())
                .orElseThrow(() -> new RuntimeException("the customer address id is not found"));

        User currentUser = userrepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("the user id is not found"));

        PaymentProvider provider = providerrepo.findByProviderName(request.getPaymentProviderName())
                .orElseThrow(() -> new RuntimeException("the payment provider name is not found"));


        Order order = new Order();
        order.setCustomer(currentUser);
        order.setBranch(branch);
        order.setRestaurant(branch.getRestaurant());
        order.setCustomerAddress(address);
        order.setDeliveryLat(request.getDeliveryLat());
        order.setDeliveryLng(request.getDeliveryLng());
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentProvider(provider);

        Integer deliveryfee = calculateDeliveryFee(branch, request);
        order.setDeliveryFee(deliveryfee);


        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal itemsSubtotal = BigDecimal.ZERO;
        int totalAmount = 0;

        for (var itemDto : request.getItems()) {
            ProductBranchDetail branchDetail = productBranchDetailRepository.findById(itemDto.getProductBranchDetailId())
                    .orElseThrow(() -> new RuntimeException("product not available in this branch"));

            if (!branchDetail.getIsAvailable()) {
                throw new RuntimeException("product not found " + branchDetail.getProduct().getName());
            }
            if (branchDetail.getStock() < itemDto.getQuantity()) {
                throw new RuntimeException("stock is not enough " + branchDetail.getProduct().getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductBranchDetail(branchDetail);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setNameSnapshot(branchDetail.getProduct().getName());
            orderItem.setImgUrlSnapshot(branchDetail.getProduct().getImgUrl());
            orderItem.setPriceSnapshot(branchDetail.getPrice().intValue());


            branchDetail.setStock(branchDetail.getStock() - itemDto.getQuantity());

            BigDecimal itemTotal = branchDetail.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            orderItem.setItemTotal(itemTotal);

            itemsSubtotal = itemsSubtotal.add(itemTotal);
            totalAmount += itemDto.getQuantity();

            orderItems.add(orderItem);
        }

        order.setAmount(totalAmount);
        order.setOrderItems(orderItems);

        int calculatedServiceFee = itemsSubtotal.multiply(new BigDecimal("0.05")).intValue();
        order.setServiceFee(calculatedServiceFee);


        BigDecimal finalTotal = itemsSubtotal
                .add(BigDecimal.valueOf(deliveryfee))
                .add(BigDecimal.valueOf(calculatedServiceFee));
        order.setTotal(finalTotal);


        Order savedOrder = orderrepo.save(order);

        deliveryAgentService.processOrderAssignment(savedOrder);

        return orderMapper.toResponse(savedOrder);
    }
    public Map<String, Object> calculateFeesOnly(OrderRequest request, Long userId) {
        RestaurantBranch branch = restbranchrepo.findById(request.getRestaurnat_branch_id())
                .orElseThrow(() -> new RuntimeException("Branch not found"));


        Integer deliveryFee = calculateDeliveryFee(branch, request);

        BigDecimal itemsSubtotal = BigDecimal.ZERO;
        for (var itemDto : request.getItems()) {
            ProductBranchDetail branchDetail = productBranchDetailRepository.findById(itemDto.getProductBranchDetailId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            BigDecimal itemTotal = branchDetail.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            itemsSubtotal = itemsSubtotal.add(itemTotal);
        }


        int serviceFee = itemsSubtotal.multiply(new BigDecimal("0.05")).intValue();


        BigDecimal finalTotal = itemsSubtotal
                .add(BigDecimal.valueOf(deliveryFee))
                .add(BigDecimal.valueOf(serviceFee));


        Map<String, Object> response = new HashMap<>();
        response.put("subtotal", itemsSubtotal);
        response.put("deliveryFee", deliveryFee);
        response.put("serviceFee", serviceFee);
        response.put("totalPrice", finalTotal);

        return response;
    }
    private Integer calculateDeliveryFee(RestaurantBranch branch, OrderRequest request) {

        final BigDecimal EARTH_RADIUS = new BigDecimal("6371.0");

        BigDecimal lat1 = branch.getLat();
        BigDecimal lon1 = branch.getLng();
        BigDecimal lat2 = request.getDeliveryLat();
        BigDecimal lon2 = request.getDeliveryLng();


        double latDistanceRad = Math.toRadians(lat2.subtract(lat1).doubleValue());
        double lonDistanceRad = Math.toRadians(lon2.subtract(lon1).doubleValue());


        double a = Math.sin(latDistanceRad / 2) * Math.sin(latDistanceRad / 2)
                + Math.cos(Math.toRadians(lat1.doubleValue())) * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.sin(lonDistanceRad / 2) * Math.sin(lonDistanceRad / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        BigDecimal distanceInKm = EARTH_RADIUS.multiply(BigDecimal.valueOf(c));


        int baseFee = 20;
        BigDecimal zero = BigDecimal.ZERO;

        if (distanceInKm.compareTo(new BigDecimal("3.0")) > 0) {
            BigDecimal extraDistance = distanceInKm.subtract(new BigDecimal("3.0"));

            int extraFee = extraDistance.multiply(new BigDecimal("5")).intValue();
            baseFee += extraFee;
        }

        return baseFee;
    }


    public List<OrderResponse> getAllOrdersOfUser(Long userId,Long restId) {

        if(!restaurantRepo.existsById(restId)) {
            throw new RuntimeException("The restaurant id is not found");
        }
        if (userrepo.findById(userId).isEmpty()) {
            throw new RuntimeException("The user id is not found");
        }

        List<Order> userOrders = orderrepo.findByCustomerIdAndRestaurantIdOrderByCreatedAtDesc(userId, restId);

        return userOrders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }


    public List<OrderResponse> getAllBranchOrders(Long branchId,Long restId) {

        if(!restaurantRepo.existsById(restId)) {
            throw new RuntimeException("The restaurant id is not found");
        }
        if (!restbranchrepo.existsById(branchId)) {
            throw new RuntimeException("The branch id is not found");
        }


        List<Order> branchOrders = orderrepo.
                findByBranchIdAndRestaurantIdOrderByCreatedAtDesc(branchId,restId);

        return branchOrders.stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    public OrderResponse updateOrderStatus(Long orderId,OrderStatus newStatus) {
      Order order=orderrepo.findById(orderId)
              .orElseThrow(() -> new RuntimeException("Order id not found"));

      order.setStatus(newStatus);
      orderrepo.save(order);
      return orderMapper.toResponse(order);


    }
    @Transactional
    public OrderDeliveryResponse getOrderDeliveryDetails(Long orderId) {
        Order order = orderrepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + orderId));

        return orderMapper.toDeliveryResponse(order);
    }


}
