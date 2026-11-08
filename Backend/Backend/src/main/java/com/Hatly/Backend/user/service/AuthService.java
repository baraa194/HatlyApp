package com.Hatly.Backend.user.service;

import com.Hatly.Backend.resturant.enums.MemberStatus;
import com.Hatly.Backend.resturant.enums.RestaurantRole;
import com.Hatly.Backend.resturant.enums.RestaurantStatus;
import com.Hatly.Backend.resturant.model.Restaurant;
import com.Hatly.Backend.resturant.model.RestaurantMember;
import com.Hatly.Backend.resturant.repo.RestMemberRepo;
import com.Hatly.Backend.resturant.repo.RestaurantRepo;
import com.Hatly.Backend.user.dto.*;
import com.Hatly.Backend.user.enums.systemRole;
import com.Hatly.Backend.user.model.PasswordReset;
import com.Hatly.Backend.user.model.User;
import com.Hatly.Backend.user.repo.PasswordResetRepo;
import com.Hatly.Backend.user.repo.UserRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {
    @Autowired
    private UserRepo userrepo;
    @Autowired
    private RestaurantRepo restaurantrepo;
    @Autowired
    AuthenticationManager authmanager;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private PasswordResetRepo passrepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private RestMemberRepo memberrepo;

    @Autowired
    private PasswordEncoder encoder;


    @Transactional
    public String register(RegisterRequest request) {
        User user = mapToUser(request);
        User savedUser = userrepo.save(user);
        if(request.getRole()==systemRole.RESTAURANT_USER)
        {
            Restaurant restaurant = mapToRestaurant(request);
            Restaurant savedRestaurant = restaurantrepo.save(restaurant);
            // set rest members
            RestaurantMember ownerMember = new RestaurantMember();
            ownerMember.setUser(savedUser);
            ownerMember.setRestaurant(savedRestaurant);
            ownerMember.setRole(RestaurantRole.OWNER);
            ownerMember.setStatus(MemberStatus.ACTIVE);

            memberrepo.save(ownerMember);
        }

        return jwtService.generateToken(savedUser.getEmail(), savedUser.getRole().name());
    }

    private User mapToUser(RegisterRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(request.getRole());

        return user;
    }
    private Restaurant mapToRestaurant(RegisterRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getRestaurant().getName());
        restaurant.setLogoUrl(request.getRestaurant().getLogoUrl());
        restaurant.setCreatedAt(LocalDateTime.now());
        restaurant.setUpdatedAt(LocalDateTime.now());
        restaurant.setStatus(RestaurantStatus.ACTIVE);
        return restaurant;
    }

    public AuthResponse Verify(LoginRequest loginreq) {
        Authentication authentication = authmanager.authenticate(
                new UsernamePasswordAuthenticationToken(loginreq.getEmail(), loginreq.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Authentication failed");
        }


        User user = userrepo.findByemail(loginreq.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));


        String accessToken = jwtService.generateToken(user.getEmail(), user.getRole().name());


        AuthResponse response = new AuthResponse();
        response.setMessage("Login successful");
        response.setAccessToken(accessToken);
        response.setRefreshToken("mock-refresh-token-for-now");


        AuthResponse.UserData userData = new AuthResponse.UserData();
        userData.setId(user.getId());
        userData.setEmail(user.getEmail());
        userData.setPhone(user.getPhone());
        userData.setSystemRole(user.getRole().name().toLowerCase());

        response.setUser(userData);

        return response;
    }

    public void forgetPassword(ForgetPasswordRequest req) {
        User user= userrepo.findByemail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String otp=String.valueOf((int)(Math.random()*900000)+100000 );
        String hashOtp=encoder.encode(otp);

        PasswordReset passwordReset=new PasswordReset();
        passwordReset.setUser(user);
        passwordReset.setOtpHash(hashOtp);
        passwordReset.setCreatedAt(LocalDateTime.now());
        passwordReset.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        passrepo.save(passwordReset);

        // send email service
     emailService.sendEmail(req.getEmail(), otp);


    }
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userrepo.findByemail(resetPasswordRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));


        PasswordReset validReset = passrepo.findFirstByUserAndConsumedAtIsNullOrderByCreatedAtDesc(user)
                .orElseThrow(() -> new RuntimeException("No active OTP request found"));


        if (!encoder.matches(resetPasswordRequest.getOtp(), validReset.getOtpHash())) {
            throw new RuntimeException("Invalid OTP code");
        }


        if (validReset.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP has expired");
        }


        user.setPassword(encoder.encode(resetPasswordRequest.getNewPassword()));
        userrepo.save(user);

        validReset.setConsumedAt(LocalDateTime.now());
        passrepo.save(validReset);
        System.out.println("OTP from Angular: " + resetPasswordRequest.getOtp());
        System.out.println("OTP Hash from DB: " + validReset.getOtpHash());
        System.out.println("Is Match?: " + encoder.matches(resetPasswordRequest.getOtp(), validReset.getOtpHash()));
    }

}