package com.erdemiryigit.brokagefirm.repository;


import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import com.erdemiryigit.brokagefirm.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerAssetRepository extends JpaRepository<CustomerAsset, Long>, JpaSpecificationExecutor<CustomerAsset> {
    Optional<CustomerAsset> findByCustomerIdAndAssetTicker(Long customerId, String assetTicker);
}
