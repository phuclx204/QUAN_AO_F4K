package org.example.quan_ao_f4k.service.product;


import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.service.CrudService;

import java.util.List;

public interface BrandService extends CrudService<Long, BrandRequest, BrandResponse> {
	BrandResponse findByName(String name);

	void updateStatus(Long id, int status);

	void exportExcel(HttpServletResponse response) throws Exception;

	void exportPdf(HttpServletResponse response) throws Exception;

	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<BrandResponse> findByStatusActive();

}
