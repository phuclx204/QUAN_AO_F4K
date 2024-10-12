package org.example.quan_ao_f4k.service.product;

import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.ProductDetailMapper;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    @Autowired
    private ProductDetailMapper productDetailMapper;

    @Autowired
    private ProductDetailRepository productDetailRepository;

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
}
