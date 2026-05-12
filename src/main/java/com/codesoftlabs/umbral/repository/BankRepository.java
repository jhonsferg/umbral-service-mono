package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankRepository extends JpaRepository<Bank, UUID> {
    List<Bank> findByCountryId(UUID countryId);
}
