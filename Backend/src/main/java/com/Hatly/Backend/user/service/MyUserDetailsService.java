package com.Hatly.Backend.user.service;

import com.Hatly.Backend.user.model.User;
import com.Hatly.Backend.user.model.UserPrinciple;
import com.Hatly.Backend.user.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepo.findByemail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email : " + email));

        return new UserPrinciple(user);
    }
}
