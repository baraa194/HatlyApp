package com.Hatly.Backend.user.repo;

import com.Hatly.Backend.user.model.PasswordReset;
import com.Hatly.Backend.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetRepo extends JpaRepository<PasswordReset,Long> {

    Optional<PasswordReset> findFirstByUserAndConsumedAtIsNullOrderByCreatedAtDesc(User user);
}
