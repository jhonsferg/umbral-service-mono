package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByOwnerId(UUID ownerId);

    Optional<Account> findByIdAndOwnerId(UUID id, UUID ownerId);
}
