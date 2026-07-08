package com.Hatly.Backend.Services;

import com.Hatly.Backend.resturant.dto.CreateRestaurantDTO;
import com.Hatly.Backend.resturant.dto.CreateRestaurantOwnerDTO;
import com.Hatly.Backend.resturant.enums.RestaurantStatus;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantMember;
import com.Hatly.Backend.resturant.repo.RestMemberRepo;
import com.Hatly.Backend.resturant.repo.RestaurantRepo;
import com.Hatly.Backend.resturant.service.RestaurantService;
import com.Hatly.Backend.user.model.User;
import com.Hatly.Backend.user.repo.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepo restaurantrepo;

    @Mock
    private RestMemberRepo memberrepo;

    @Mock
    private UserRepo userrepo;

    @InjectMocks
    private RestaurantService restaurantService;

    @Test
    public void createRestaurant_Success() {
        CreateRestaurantOwnerDTO ownerDTO = new CreateRestaurantOwnerDTO();
        ownerDTO.setName("Baraa");
        ownerDTO.setEmail("baraa@example.com");
        ownerDTO.setPhone("0123456789");
        ownerDTO.setPassword("securePassword123");

        CreateRestaurantDTO request = new CreateRestaurantDTO();
        request.setName("Hatly Burgers");
        request.setLogoUrl("logo.jpg");
        request.setOwner(ownerDTO);

        Restaurant mockSavedRestaurant = new Restaurant();
        mockSavedRestaurant.setId(1L);
        mockSavedRestaurant.setName("Hatly Burgers");
        mockSavedRestaurant.setStatus(RestaurantStatus.ACTIVE);

        User mockSavedUser = new User();
        mockSavedUser.setId(10L);
        mockSavedUser.setName("Baraa");
        mockSavedUser.setEmail("baraa@example.com");

        Mockito.when(restaurantrepo.save(any(Restaurant.class))).thenReturn(mockSavedRestaurant);
        Mockito.when(userrepo.save(any(User.class))).thenReturn(mockSavedUser);
        Mockito.when(memberrepo.save(any(RestaurantMember.class))).thenReturn(new RestaurantMember());

        Map<String, Object> result = restaurantService.createRestaurant(request);

        assertNotNull(result);
        assertTrue(result.containsKey("restaurant"));
        assertTrue(result.containsKey("Owner"));

        Restaurant returnedRestaurant = (Restaurant) result.get("restaurant");
        User returnedUser = (User) result.get("Owner");

        assertEquals(1L, returnedRestaurant.getId());
        assertEquals("Hatly Burgers", returnedRestaurant.getName());
        assertEquals(10L, returnedUser.getId());
        assertEquals("Baraa", returnedUser.getName());

        Mockito.verify(restaurantrepo, Mockito.times(1)).save(any(Restaurant.class));
        Mockito.verify(userrepo, Mockito.times(1)).save(any(User.class));
        Mockito.verify(memberrepo, Mockito.times(1)).save(any(RestaurantMember.class));
    }
}