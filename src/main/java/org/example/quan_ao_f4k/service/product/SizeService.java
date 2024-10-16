package org.example.quan_ao_f4k.service.product;


import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.dto.request.product.SizeRequest;
import org.example.quan_ao_f4k.dto.response.product.ColorResponse;
import org.example.quan_ao_f4k.dto.response.product.SizeResponse;
import org.example.quan_ao_f4k.service.CrudService;

import java.util.List;

public interface SizeService extends CrudService<Long, SizeRequest, SizeResponse> {
	void updateStatus(Long id, int status);
	void exportExcel(HttpServletResponse response) throws Exception;
	void exportPdf(HttpServletResponse response) throws Exception;
	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
	List<SizeResponse> findByStatusActive();
}
