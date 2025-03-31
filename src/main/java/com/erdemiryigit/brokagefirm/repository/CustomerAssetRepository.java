package com.erdemiryigit.brokagefirm.repository;


import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerAssetRepository extends JpaRepository<CustomerAsset, UUID>, JpaSpecificationExecutor<CustomerAsset> {
    Optional<CustomerAsset> findByCustomerIdAndAssetTicker(UUID customerId, String assetTicker);
}
