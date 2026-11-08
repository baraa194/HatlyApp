package com.Hatly.Backend.Services;

import com.Hatly.Backend.product.dto.ProductRequest;
import com.Hatly.Backend.product.dto.ProductResponse;
import com.Hatly.Backend.product.model.Product;
import com.Hatly.Backend.product.model.ProductCategory;
import com.Hatly.Backend.product.model.ProductBranchDetail;
import com.Hatly.Backend.product.repo.ProductRepo;
import com.Hatly.Backend.product.repo.ProductCategoryRepo;
import com.Hatly.Backend.product.repo.ProductBranchRepo;
import com.Hatly.Backend.product.service.ProductService;
import com.Hatly.Backend.product.mapper.ProductResponseMapper;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantBranch;
import com.Hatly.Backend.resturant.repo.RestaurantRepo;
import com.Hatly.Backend.resturant.repo.RestBranchRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private RestaurantRepo restaurantRepo;

    @Mock
    private ProductCategoryRepo productCategoryRepo;

    @Mock
    private ProductRepo productRepo;

    @Mock
    private RestBranchRepo branchRepo;

    @Mock
    private ProductBranchRepo productBranchrepo;

    @Mock
    private ProductResponseMapper responseMapper;

    @InjectMocks
    private ProductService productService;

    @Test
    public void createProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Pizza");
        request.setDescription("Cheese Pizza");
        request.setImgUrl("pizza.jpg");

        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setId(10L);

        ProductCategory mockCategory = new ProductCategory();

        Product mockSavedProduct = new Product();
        mockSavedProduct.setId(100L);
        mockSavedProduct.setName("Pizza");

        RestaurantBranch branch1 = new RestaurantBranch();
        RestaurantBranch branch2 = new RestaurantBranch();
        List<RestaurantBranch> mockBranches = List.of(branch1, branch2);

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(100L);
        mockResponse.setName("Pizza");

        Mockito.when(restaurantRepo.findById(10L)).thenReturn(Optional.of(mockRestaurant));
        Mockito.when(productCategoryRepo.findByNameAndRestaurantId(any(), Mockito.eq(10L))).thenReturn(Optional.of(mockCategory));
        Mockito.when(productRepo.save(any(Product.class))).thenReturn(mockSavedProduct);
        Mockito.when(branchRepo.findByRestaurantId(10L)).thenReturn(mockBranches);
        Mockito.when(responseMapper.toResponse(mockSavedProduct)).thenReturn(mockResponse);

        ProductResponse response = productService.CreateProduct(10L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Pizza", response.getName());

        Mockito.verify(productRepo, Mockito.times(1)).save(any(Product.class));
        Mockito.verify(productBranchrepo, Mockito.times(1)).saveAll(anyList());
    }

    @Test
    public void createProduct_ThrowsException_WhenRestaurantNotFound() {
        ProductRequest request = new ProductRequest();

        Mockito.when(restaurantRepo.findById(10L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.CreateProduct(10L, request);
        });

        assertTrue(exception.getMessage().contains("Restaurant not found"));
        Mockito.verify(productRepo, Mockito.never()).save(any(Product.class));
        Mockito.verify(productBranchrepo, Mockito.never()).saveAll(anyList());
    }
}