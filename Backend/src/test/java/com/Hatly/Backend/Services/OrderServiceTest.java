package com.Hatly.Backend.Services;

import com.Hatly.Backend.deliveryAgent.service.DeliveryAgentService;
import com.Hatly.Backend.order.dto.OrderItemRequest;
import com.Hatly.Backend.order.dto.OrderRequest;
import com.Hatly.Backend.order.dto.OrderResponse;
import com.Hatly.Backend.order.enums.OrderStatus;
import com.Hatly.Backend.order.model.Order;
import com.Hatly.Backend.order.repo.OrderRepo;
import com.Hatly.Backend.order.service.OrderService;
import com.Hatly.Backend.order.mapper.OrderMapper;
import com.Hatly.Backend.payment.enums.PaymentProviderName;
import com.Hatly.Backend.payment.model.PaymentProvider;
import com.Hatly.Backend.payment.repo.PaymentProviderRepo;
import com.Hatly.Backend.product.model.Product;
import com.Hatly.Backend.product.model.ProductBranchDetail;
import com.Hatly.Backend.product.repo.ProductBranchRepo;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import com.Hatly.Backend.resturant.repo.RestBranchRepo;
import com.Hatly.Backend.user.model.CustomerAddress;
import com.Hatly.Backend.user.model.User;
import com.Hatly.Backend.user.repo.CustomerAddresseRepo;
import com.Hatly.Backend.user.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepo orderrepo;

    @Mock
    private ProductBranchRepo productBranchDetailRepository;

    @Mock
    private RestBranchRepo restbranchrepo;

    @Mock
    private CustomerAddresseRepo customeraddressrepo;

    @Mock
    private UserRepo userrepo;

    @Mock
    private PaymentProviderRepo providerrepo;

    @Mock
    private DeliveryAgentService deliveryAgentService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    @Test
    public void createOrder_Success_WhenStockIsAvailable() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductBranchDetailId(200L);
        itemRequest.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setRestaurnat_branch_id(10L);
        request.setCustomerAddress_id(50L);
        request.setPaymentProviderName(PaymentProviderName.CASH);
        request.setDeliveryLat(BigDecimal.valueOf(30.0));
        request.setDeliveryLng(BigDecimal.valueOf(31.0));
        request.setItems(List.of(itemRequest));

        ProductBranchDetail mockProductDetail = new ProductBranchDetail();
        mockProductDetail.setId(200L);
        mockProductDetail.setStock(10L);
        mockProductDetail.setPrice(BigDecimal.valueOf(100));
        mockProductDetail.setIsAvailable(true);

        Product baseProduct = new Product();
        baseProduct.setName("Burger");
        baseProduct.setImgUrl("burger.jpg");
        mockProductDetail.setProduct(baseProduct);

        RestaurantBranch mockBranch = new RestaurantBranch();
        mockBranch.setLat(BigDecimal.valueOf(30.0));
        mockBranch.setLng(BigDecimal.valueOf(31.0));

        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId(1L);
        mockResponse.setStatus(OrderStatus.PENDING);
        mockResponse.setTotal(BigDecimal.valueOf(200));

        Mockito.when(restbranchrepo.findById(10L)).thenReturn(Optional.of(mockBranch));
        Mockito.when(customeraddressrepo.findById(50L)).thenReturn(Optional.of(new CustomerAddress()));
        Mockito.when(userrepo.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(providerrepo.findByProviderName(PaymentProviderName.CASH)).thenReturn(Optional.of(new PaymentProvider()));
        Mockito.when(productBranchDetailRepository.findById(200L)).thenReturn(Optional.of(mockProductDetail));

        Mockito.when(orderrepo.save(any(Order.class))).thenAnswer(invocation -> {
            Order orderToSave = invocation.getArgument(0);
            orderToSave.setId(1L);
            orderToSave.setStatus(OrderStatus.PENDING);
            return orderToSave;
        });

        Mockito.when(orderMapper.toResponse(any(Order.class))).thenReturn(mockResponse);

        OrderResponse response = orderService.createOrder(request, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(OrderStatus.PENDING, response.getStatus());
        assertTrue(BigDecimal.valueOf(200).compareTo(response.getTotal()) == 0);
    }

    @Test
    public void createOrder_ThrowsException_WhenStockIsNotEnough() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductBranchDetailId(200L);
        itemRequest.setQuantity(5);

        OrderRequest request = new OrderRequest();
        request.setRestaurnat_branch_id(10L);
        request.setCustomerAddress_id(50L);
        request.setPaymentProviderName(PaymentProviderName.CASH);
        request.setDeliveryLat(BigDecimal.valueOf(30.0));
        request.setDeliveryLng(BigDecimal.valueOf(31.0));
        request.setItems(List.of(itemRequest));

        ProductBranchDetail mockProductDetail = new ProductBranchDetail();
        mockProductDetail.setId(200L);
        mockProductDetail.setStock(2L);
        mockProductDetail.setIsAvailable(true);

        Product baseProduct = new Product();
        baseProduct.setName("Burger");
        mockProductDetail.setProduct(baseProduct);

        RestaurantBranch mockBranch = new RestaurantBranch();
        mockBranch.setLat(BigDecimal.valueOf(30.0));
        mockBranch.setLng(BigDecimal.valueOf(31.0));

        Mockito.when(restbranchrepo.findById(10L)).thenReturn(Optional.of(mockBranch));
        Mockito.when(customeraddressrepo.findById(50L)).thenReturn(Optional.of(new CustomerAddress()));
        Mockito.when(userrepo.findById(1L)).thenReturn(Optional.of(new User()));
        Mockito.when(providerrepo.findByProviderName(PaymentProviderName.CASH)).thenReturn(Optional.of(new PaymentProvider()));
        Mockito.when(productBranchDetailRepository.findById(200L)).thenReturn(Optional.of(mockProductDetail));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.createOrder(request, 1L);
        });

        assertTrue(exception.getMessage().contains("stock") || exception.getMessage().contains("not enough"));
        Mockito.verify(orderrepo, Mockito.never()).save(any(Order.class));
    }
}