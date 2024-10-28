package org.example.quan_ao_f4k.mapper.order;

import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderDetailResponse;
import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = MapperCoverter.class)
public interface OrderDetailMapper
        extends GennericMapper<OrderDetail, OrderDetailRequest, OrderDetailResponse> {

    @Override
    @Mapping(source = "orderId", target = "order", qualifiedByName = "convertToOrder")
    @Mapping(source = "productDetailId", target = "productDetail", qualifiedByName = "convertToProductDetail")
    OrderDetail requestToEntity(OrderDetailRequest request);

    @Override
    @Mapping(source = "orderId", target = "order", qualifiedByName = "convertToOrder")
    @Mapping(source = "productDetailId", target = "productDetail", qualifiedByName = "convertToProductDetail")
    OrderDetail partialUpdate(@MappingTarget OrderDetail entity, OrderDetailRequest request);

}
