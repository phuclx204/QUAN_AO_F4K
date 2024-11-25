package org.example.quan_ao_f4k.service.product;

import jakarta.servlet.http.HttpServletResponse;
import org.example.quan_ao_f4k.dto.request.product.ProductRequest;
import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService extends CrudService<Long, ProductRequest, ProductResponse> {
	void updateStatus(Long id, int status);
	void exportExcel(HttpServletResponse response) throws Exception;
	void exportPdf(HttpServletResponse response) throws Exception;
	boolean isAddExistProductByBrandAndCate(String name,Long brandId, Long categoryId);
	boolean isUpdateExistProductByBrandAndCate(String name,Long brandId, Long categoryId, Long id);

	Page<ProductResponse> searchProducts(int page, int size, String search, Integer status, Long categoryId, Long brandId);
	void addProduct(ProductRequest productRequest);
	void updateProduct(Long id, ProductRequest productRequest);
	ProductResponse getDetail(Long id);
}
