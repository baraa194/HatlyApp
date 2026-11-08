package com.Hatly.Backend.user.repo;

import com.Hatly.Backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User,Integer> {

    Optional<User> findByemail(String email);
    Optional<User> findById(Long id);



}
