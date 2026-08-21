package com.Hatly.Backend.Services;

import com.Hatly.Backend.deliveryAgent.dto.AgentOrderActionRequest;
import com.Hatly.Backend.deliveryAgent.dto.UpdateDeliveryStatusRequest;
import com.Hatly.Backend.deliveryAgent.dto.UpdateLocationRequest;
import com.Hatly.Backend.deliveryAgent.enums.AgentStatus;
import com.Hatly.Backend.deliveryAgent.model.AgentPresence;
import com.Hatly.Backend.deliveryAgent.model.DeliveryAgent;
import com.Hatly.Backend.deliveryAgent.repo.AgentPresenceRepo;
import com.Hatly.Backend.deliveryAgent.repo.DeliveryAgentRepo;
import com.Hatly.Backend.deliveryAgent.service.DeliveryAgentService;
import com.Hatly.Backend.exceptions.TooManyRequestsException;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryAgentServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private GeoOperations<String, String> geoOperations;

    @Mock
    private DeliveryAgentRepo deliveryAgentRepo;

    @Mock
    private AgentPresenceRepo agentPresenceRepo;

    @Mock
    private OrderRepo orderrepo;

    @InjectMocks
    private DeliveryAgentService deliveryAgentService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForGeo()).thenReturn(geoOperations);
    }
    @Test
    public void handleOrderAction_Accept_Success() {
        AgentOrderActionRequest request = new AgentOrderActionRequest();
        request.setAction("ACCEPT");

        Order mockOrder = new Order();
        mockOrder.setId(100L);
        mockOrder.setStatus(OrderStatus.PENDING);

        DeliveryAgent mockAgent = new DeliveryAgent();
        mockAgent.setId(1L);
        mockAgent.setStatus(AgentStatus.AVAILABLE);

        when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));
        when(deliveryAgentRepo.findByUserId(1L)).thenReturn(Optional.of(mockAgent));

        deliveryAgentService.handleOrderAction(100L, 1L, request);

        assertEquals(mockAgent, mockOrder.getDeliveryAgent());
        assertEquals(OrderStatus.READY_FOR_PICKUP, mockOrder.getStatus());
        assertEquals(AgentStatus.BUSY, mockAgent.getStatus());

        verify(orderrepo, Mockito.times(1)).save(mockOrder);
        verify(deliveryAgentRepo, Mockito.times(1)).save(mockAgent);
    }

    @Test
    public void handleOrderAction_Accept_ThrowsException_WhenOrderAlreadyAccepted() {
        AgentOrderActionRequest request = new AgentOrderActionRequest();
        request.setAction("ACCEPT");

        Order mockOrder = new Order();
        mockOrder.setId(100L);
        mockOrder.setDeliveryAgent(new DeliveryAgent());

        DeliveryAgent mockAgent = new DeliveryAgent();
        mockAgent.setId(1L);

        when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));
        when(deliveryAgentRepo.findByUserId(1L)).thenReturn(Optional.of(mockAgent));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryAgentService.handleOrderAction(100L, 1L, request);
        });

        assertTrue(exception.getMessage().contains("already been accepted"));
        verify(orderrepo, Mockito.never()).save(any(Order.class));
    }

    @Test
    public void updateDeliveryStatus_OutForDelivery_Success() {
        UpdateDeliveryStatusRequest request = new UpdateDeliveryStatusRequest();
        request.setStatus("OUT_FOR_DELIVERY");

        User mockUser = new User();
        mockUser.setId(1L);

        DeliveryAgent mockAgent = new DeliveryAgent();
        mockAgent.setUser(mockUser);

        Order mockOrder = new Order();
        mockOrder.setId(100L);
        mockOrder.setDeliveryAgent(mockAgent);
        mockOrder.setStatus(OrderStatus.READY_FOR_PICKUP);

        when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));

        deliveryAgentService.updateDeliveryStatus(100L, 1L, request);

        assertEquals(OrderStatus.OUT_FOR_DELIVERY, mockOrder.getStatus());
        verify(orderrepo, Mockito.times(1)).save(mockOrder);
    }

    @Test
    public void updateDeliveryStatus_Delivered_Success() {
        UpdateDeliveryStatusRequest request = new UpdateDeliveryStatusRequest();
        request.setStatus("DELIVERED");

        User mockUser = new User();
        mockUser.setId(1L);

        DeliveryAgent mockAgent = new DeliveryAgent();
        mockAgent.setUser(mockUser);
        mockAgent.setStatus(AgentStatus.BUSY);

        Order mockOrder = new Order();
        mockOrder.setId(100L);
        mockOrder.setDeliveryAgent(mockAgent);
        mockOrder.setStatus(OrderStatus.OUT_FOR_DELIVERY);

        when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));

        deliveryAgentService.updateDeliveryStatus(100L, 1L, request);

        assertEquals(OrderStatus.DELIVERED, mockOrder.getStatus());
        assertEquals(AgentStatus.AVAILABLE, mockAgent.getStatus());

        verify(orderrepo, Mockito.times(1)).save(mockOrder);
        verify(deliveryAgentRepo, Mockito.times(1)).save(mockAgent);
    }

    @Test
    public void updateDeliveryStatus_ThrowsException_WhenUnauthorizedAgent() {
        UpdateDeliveryStatusRequest request = new UpdateDeliveryStatusRequest();
        request.setStatus("DELIVERED");

        User mockUser = new User();
        mockUser.setId(1L);

        DeliveryAgent assignedAgent = new DeliveryAgent();
        assignedAgent.setUser(mockUser);

        Order mockOrder = new Order();
        mockOrder.setId(100L);
        mockOrder.setDeliveryAgent(assignedAgent);

        when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryAgentService.updateDeliveryStatus(100L, 2L, request);
        });

        assertTrue(exception.getMessage().contains("Unauthorized"));
        verify(orderrepo, Mockito.never()).save(any(Order.class));
    }
    @Test
    void updateAgentLocation_success() {

        Long agentId = 123L;
        UpdateLocationRequest request = new UpdateLocationRequest();
        request.setLastLat(new BigDecimal("30.0444"));
        request.setLastLng(new BigDecimal("31.2357"));
        User mockUser = new User();
        mockUser.setId(agentId);

        DeliveryAgent mockAgent = new DeliveryAgent();
        mockAgent.setId(10L);
        mockAgent.setUser(mockUser);

        AgentPresence mockPresence = new AgentPresence();



        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(true);

        when(deliveryAgentRepo.findByUserId(agentId))
                .thenReturn(Optional.of(mockAgent));

        when(agentPresenceRepo.findByAgentId(mockAgent.getId()))
                .thenReturn(Optional.of(mockPresence));


        deliveryAgentService.updateAgentLocation(agentId, request);


        verify(valueOperations).setIfAbsent(
                eq("rate:location:" + agentId),
                eq("1"),
                any(Duration.class)
        );

        verify(geoOperations).add(
                eq("ACTIVE_AGENTS_LOCATIONS"),
                any(Point.class),
                eq(agentId.toString())
        );

        verify(agentPresenceRepo).save(any(AgentPresence.class));

    }
    @Test
    void updateAgentLocation_rateLimited_throwsException() {

        Long agentId = 123L;
        UpdateLocationRequest request = new UpdateLocationRequest();
        request.setLastLat(new BigDecimal("30.0444"));
        request.setLastLng(new BigDecimal("31.2357"));

        when(valueOperations.setIfAbsent(anyString(), eq("1"), any(Duration.class)))
                .thenReturn(false);


        assertThrows(TooManyRequestsException.class, () ->
                deliveryAgentService.updateAgentLocation(agentId, request)
        );

        verify(deliveryAgentRepo, never()).findByUserId(any());
        verify(geoOperations, never()).add(any(), any(), any());
        verify(agentPresenceRepo, never()).save(any());
    }
}