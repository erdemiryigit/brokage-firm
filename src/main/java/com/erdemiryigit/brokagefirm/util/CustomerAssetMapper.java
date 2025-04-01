package com.erdemiryigit.brokagefirm.util;

import com.erdemiryigit.brokagefirm.dto.response.CustomerAssetGetResponse;
import com.erdemiryigit.brokagefirm.entity.CustomerAsset;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CustomerAssetMapper {

    @Mapping(target = "customerName", source = "customer.name")
    @Mapping(target = "ticker", source = "asset.ticker")
    @Mapping(target = "description", source = "asset.description")
    public abstract CustomerAssetGetResponse toCustomerAssetGetResponse(CustomerAsset customerAsset);

    public abstract List<CustomerAssetGetResponse> toCustomerAssetGetResponseList(List<CustomerAsset> customerAssetList);

}
