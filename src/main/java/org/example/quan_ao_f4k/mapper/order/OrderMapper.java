package org.example.quan_ao_f4k.mapper.order;


import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.mapper.address.AddressMapper;

import org.example.quan_ao_f4k.model.order.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring"
        , unmappedTargetPolicy = ReportingPolicy.IGNORE
        , uses = {MapperCoverter.class,PaymentMethodMapper.class})
public interface OrderMapper extends GennericMapper<Order, OrderRequest, OrderResponse> {
    @Override
    @Mapping(source = "order_type", target = "order_type")
//    @Mapping(source = "userId", target = "user", qualifiedByName = "convertToUser")
    Order requestToEntity(OrderRequest request);

    @Override
    @Mapping(source = "order_type", target = "order_type")
//    @Mapping(source = "userId", target = "user", qualifiedByName = "convertToUser")
    @Mapping(source = "paymentMethodType", target = "paymentMethod", qualifiedByName = "convertToPayment")
    Order partialUpdate(@MappingTarget Order entity, OrderRequest request);
}
