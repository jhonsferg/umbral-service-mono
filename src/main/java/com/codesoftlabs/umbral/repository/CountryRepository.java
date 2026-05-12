package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CountryRepository extends JpaRepository<Country, UUID> {
    List<Country> findByTimezoneId(UUID timezoneId);
}
