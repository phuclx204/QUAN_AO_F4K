package org.example.quan_ao_f4k.service.product;

import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.ProductDetailMapper;
import org.example.quan_ao_f4k.mapper.product.ProductDetailMapperImpl;
import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.model.product.Size;
import org.example.quan_ao_f4k.repository.product.ColorRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.repository.product.SizeRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.example.quan_ao_f4k.util.SearchUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    @Autowired
    private ProductDetailMapper productDetailMapper;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private ProductDetailMapperImpl productDetailMapperImpl;

    @Override
    public ListResponse<ProductDetailResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.PRODUCT_DETAIL, productDetailRepository, productDetailMapper);
    }

    @Override
    public ProductDetailResponse findById(Long aLong) {
        return findById(aLong);
    }

    @Override
    public ProductDetailResponse save(ProductDetailRequest request) {
        return save(request);
    }

    @Override
    public ProductDetailResponse save(Long aLong, ProductDetailRequest request) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(List<Long> longs) {

    }


    @Override
    public ListResponse<ProductDetailResponse> getProductDetailByProductId(
            Long productId, int page, int size, String sort,
            String filter, String search, boolean all) {

        return defaultFindDetailsByProductId(
                productId, page, size, sort, filter, search, all,
                SearchFields.PRODUCT_DETAIL, productDetailRepository, productDetailMapper, "productDetail");
    }

    @Override
    public ProductDetailResponse addProductDetail(Long productId, ProductDetailRequest request) {
        request.setProductId(productId);
        return defaultSave(request, productDetailRepository, productDetailMapper);
    }

    @Override
    public ProductDetailResponse updateProductDetail(Long productId, Long id, ProductDetailRequest request) {
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new RuntimeException("Kích thước không tồn tại"));
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
        productDetail.setProduct(product);
        productDetail.setSize(size);
        productDetail.setColor(color);
        productDetail.setPrice(request.getPrice());
        productDetail.setQuantity(request.getQuantity());
        productDetail.setStatus(request.getStatus());

        productDetailRepository.save(productDetail);

        return productDetailMapper.entityToResponse(productDetail);
    }

    @Override
    public boolean isAddExistsByProductSizeAndColor(Long productId,Long sizeId, Long colorId) {
        return productDetailRepository.isAddExistsByProductSizeAndColor(productId,sizeId, colorId);
    }
    @Override
    public boolean isUpdateExistsByProductSizeAndColor(Long productId,Long sizeId, Long colorId,Long id) {
        return productDetailRepository.isUpdateExistsByProductSizeAndColorId(productId,sizeId, colorId,id);

    }
}
