package org.example.quan_ao_f4k.service.product;

import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.dto.request.product.ProductRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.service.CrudService;

import java.util.List;

public interface ProductService extends CrudService<Long, ProductRequest, ProductResponse> {
	void updateStatus(Long id, int status);
	void exportExcel(HttpServletResponse response) throws Exception;
	void exportPdf(HttpServletResponse response) throws Exception;
	boolean existsProductNamesByBrandAndCategory(String name,Long brandId, Long categoryId);
}
