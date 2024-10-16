package org.example.quan_ao_f4k.service.product;

import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.service.CrudService;

import java.util.List;

public interface CategoryService extends CrudService<Long, CategoryRequest, CategoryResponse> {
	void updateStatus(Long id, int status);

	void exportExcel(HttpServletResponse response) throws Exception;

	void exportPdf(HttpServletResponse response) throws Exception;

	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<CategoryResponse> findByStatusActive();

}
