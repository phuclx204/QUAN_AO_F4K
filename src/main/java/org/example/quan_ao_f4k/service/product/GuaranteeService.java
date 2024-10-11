package org.example.quan_ao_f4k.service.product;


import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.dto.request.product.GuaranteeRequest;
import org.example.quan_ao_f4k.dto.response.product.GuaranteeResponse;
import org.example.quan_ao_f4k.service.CrudService;

public interface GuaranteeService extends CrudService<Long, GuaranteeRequest, GuaranteeResponse
		> {
	GuaranteeResponse findByName(String name);

	void updateStatus(Long id, int status);

	void exportExcel(HttpServletResponse response) throws Exception;

	void exportPdf(HttpServletResponse response) throws Exception;

	boolean existsByName(String name);
	boolean existsByNameAndIdNot(String name,Long id);
}
