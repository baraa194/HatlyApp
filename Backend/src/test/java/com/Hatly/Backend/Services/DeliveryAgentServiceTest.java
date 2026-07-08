package com.Hatly.Backend.Services;

import com.Hatly.Backend.deliveryAgent.dto.AgentOrderActionRequest;
import com.Hatly.Backend.deliveryAgent.dto.UpdateDeliveryStatusRequest;
import com.Hatly.Backend.deliveryAgent.enums.AgentStatus;
import com.Hatly.Backend.deliveryAgent.model.DeliveryAgent;
import com.Hatly.Backend.deliveryAgent.repo.AgentPresenceRepo;
import com.Hatly.Backend.deliveryAgent.repo.DeliveryAgentRepo;
import com.Hatly.Backend.deliveryAgent.service.DeliveryAgentService;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DeliveryAgentServiceTest {

    @Mock
    private DeliveryAgentRepo deliveryAgentRepo;

    @Mock
    private AgentPresenceRepo agentPresenceRepo;

    @Mock
    private OrderRepo orderrepo;

    @InjectMocks
    private DeliveryAgentService deliveryAgentService;

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

        Mockito.when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));
        Mockito.when(deliveryAgentRepo.findByUserId(1L)).thenReturn(Optional.of(mockAgent));

        deliveryAgentService.handleOrderAction(100L, 1L, request);

        assertEquals(mockAgent, mockOrder.getDeliveryAgent());
        assertEquals(OrderStatus.READY_FOR_PICKUP, mockOrder.getStatus());
        assertEquals(AgentStatus.BUSY, mockAgent.getStatus());

        Mockito.verify(orderrepo, Mockito.times(1)).save(mockOrder);
        Mockito.verify(deliveryAgentRepo, Mockito.times(1)).save(mockAgent);
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

        Mockito.when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));
        Mockito.when(deliveryAgentRepo.findByUserId(1L)).thenReturn(Optional.of(mockAgent));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryAgentService.handleOrderAction(100L, 1L, request);
        });

        assertTrue(exception.getMessage().contains("already been accepted"));
        Mockito.verify(orderrepo, Mockito.never()).save(any(Order.class));
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

        Mockito.when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));

        deliveryAgentService.updateDeliveryStatus(100L, 1L, request);

        assertEquals(OrderStatus.OUT_FOR_DELIVERY, mockOrder.getStatus());
        Mockito.verify(orderrepo, Mockito.times(1)).save(mockOrder);
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

        Mockito.when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));

        deliveryAgentService.updateDeliveryStatus(100L, 1L, request);

        assertEquals(OrderStatus.DELIVERED, mockOrder.getStatus());
        assertEquals(AgentStatus.AVAILABLE, mockAgent.getStatus());

        Mockito.verify(orderrepo, Mockito.times(1)).save(mockOrder);
        Mockito.verify(deliveryAgentRepo, Mockito.times(1)).save(mockAgent);
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

        Mockito.when(orderrepo.findById(100L)).thenReturn(Optional.of(mockOrder));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            deliveryAgentService.updateDeliveryStatus(100L, 2L, request);
        });

        assertTrue(exception.getMessage().contains("Unauthorized"));
        Mockito.verify(orderrepo, Mockito.never()).save(any(Order.class));
    }
}