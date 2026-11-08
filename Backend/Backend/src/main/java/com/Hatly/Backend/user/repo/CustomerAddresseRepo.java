package com.Hatly.Backend.user.repo;

import com.Hatly.Backend.user.model.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerAddresseRepo extends JpaRepository<CustomerAddress,Long> {

    List<CustomerAddress> findByUserId(Long userId);
    Optional<CustomerAddress>  findById(Long id);
    Optional<CustomerAddress> findByUserIdAndIsDefaultTrue(Long userId);
}
