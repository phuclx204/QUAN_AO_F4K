package org.example.quan_ao_f4k.service.product;

import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.service.CrudService;

public interface CategoryService extends CrudService<Long, CategoryRequest, CategoryResponse> {
}
