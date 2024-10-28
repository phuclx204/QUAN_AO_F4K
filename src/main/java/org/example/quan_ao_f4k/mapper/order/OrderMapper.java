package org.example.quan_ao_f4k.mapper.order;


import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderResponse;
import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.mapper.address.AddressMapper;

import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring"
        , unmappedTargetPolicy = ReportingPolicy.IGNORE
        , uses = {MapperCoverter.class, AddressMapper.class})
public interface OrderMapper extends GennericMapper<Order, OrderRequest, OrderResponse> {

    @Override
    @Mapping(source = "order_type", target = "order_type") // Ánh xạ order_type
//    @Mapping(source = "userId", target = "user", qualifiedByName = "convertToUser")
    Order requestToEntity(OrderRequest request);
}
