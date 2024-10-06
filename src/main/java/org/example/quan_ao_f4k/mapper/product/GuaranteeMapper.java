package org.example.quan_ao_f4k.mapper.product;

import org.example.quan_ao_f4k.dto.request.product.GuaranteeRequest;
import org.example.quan_ao_f4k.dto.request.product.ProductRequest;
import org.example.quan_ao_f4k.dto.response.product.GuaranteeResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.product.Guarantee;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.service.GenericService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
        ,uses = {MapperCoverter.class}
)
public interface GuaranteeMapper extends GennericMapper<Guarantee, GuaranteeRequest, GuaranteeResponse> {
    @Override
//    @Mapping(source = "categoryId", target = "category", qualifiedByName = "convertToCategory")
    Guarantee requestToEntity(GuaranteeRequest request);


    @Override
//    @Mapping(source = "categoryId", target = "category", qualifiedByName = "convertToCategory")
    Guarantee partialUpdate(@MappingTarget Guarantee entity, GuaranteeRequest request);

}
