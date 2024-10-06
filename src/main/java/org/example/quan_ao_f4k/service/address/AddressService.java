package org.example.quan_ao_f4k.service.address;

import org.example.quan_ao_f4k.dto.request.address.AddressRequest;
import org.example.quan_ao_f4k.dto.response.address.AddressResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.example.quan_ao_f4k.service.GenericService;

public interface AddressService extends CrudService<Long, AddressRequest, AddressResponse> {
}
