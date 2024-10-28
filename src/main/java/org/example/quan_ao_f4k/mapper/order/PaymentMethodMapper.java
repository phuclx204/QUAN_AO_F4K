package org.example.quan_ao_f4k.mapper.order;

import org.example.quan_ao_f4k.dto.request.order.PaymentMethodRequest;
import org.example.quan_ao_f4k.dto.response.orders.PaymentMethodResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.model.order.PaymentMethod;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMethodMapper extends GennericMapper<PaymentMethod, PaymentMethodRequest, PaymentMethodResponse> {

}
