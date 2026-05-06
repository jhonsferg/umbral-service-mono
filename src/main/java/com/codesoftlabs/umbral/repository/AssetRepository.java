package com.codesoftlabs.umbral.repository;

import com.codesoftlabs.umbral.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssetRepository extends JpaRepository<Asset, UUID> {
    List<Asset> findByUserIdOrderByNameAsc(UUID userId);

    Optional<Asset> findByIdAndUserId(UUID id, UUID userId);
}
