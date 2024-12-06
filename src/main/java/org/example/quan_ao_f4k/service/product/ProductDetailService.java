package org.example.quan_ao_f4k.service.product;

import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ProductDetailService extends CrudService<Long, ProductDetailRequest, ProductDetailResponse> {
	ListResponse<ProductDetailResponse> getProductDetailByProductId(
			Long productId, int page, int size, String sort, String filter, String search, boolean all);
	ProductDetailResponse addProductDetail(Long productId, ProductDetailRequest request);
	ProductDetailResponse updateProductDetail(Long productId,Long id,ProductDetailRequest request);
	boolean isAddExistsByProductSizeAndColor(Long productId,Long sizeId, Long colorId);
	boolean isUpdateExistsByProductSizeAndColor(Long productId,Long sizeId, Long colorId,Long id);
	boolean deleteProductDetail(Long productId, Long id);
	Integer getQuantity(Long productDetailId);

	Page<ProductDetailResponse> searchProductDetail(int page, int size, String name, List<Long> brandIds, List<Long> categoryIds, List<Long> sizeIds, List<Long> colorIds, BigDecimal priceFrom, BigDecimal priceTo, String orderBy);
	Page<ProductDetailResponse> getList(int page, int size, String orderBy);
}
