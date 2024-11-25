package org.example.quan_ao_f4k.mapper.order;

import org.example.quan_ao_f4k.dto.request.order.OrderDetailRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderHistoryRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderHistoryResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.mapper.MapperCoverter;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,uses = {MapperCoverter.class,OrderMapper.class})
public interface OrderHistoryMapper extends GennericMapper<OrderHistory, OrderHistoryRequest, OrderHistoryResponse> {

    @Override
    @Mapping(source = "orderId", target = "order", qualifiedByName = "convertToOrder")
    OrderHistory requestToEntity(OrderHistoryRequest request);

    @Override
    @Mapping(source = "order", target = "orderType", qualifiedByName = "getOrderType")
    OrderHistoryResponse entityToResponse(OrderHistory orderHistory);

}
