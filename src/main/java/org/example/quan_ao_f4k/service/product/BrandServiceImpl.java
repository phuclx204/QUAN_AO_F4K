package org.example.quan_ao_f4k.service.product;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.BrandMapper;
import org.example.quan_ao_f4k.repository.product.BrandRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BrandServiceImpl implements BrandService{
    private BrandMapper brandMapper;
    private BrandRepository brandRepository;

    @Override
    public ListResponse<BrandResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page,size,sort,filter,search,all, SearchFields.BRAND,brandRepository,brandMapper);

    }

    @Override
    public BrandResponse findById(Long aLong) {
        return findById(aLong);
    }

    @Override
    public BrandResponse save(BrandRequest request) {
        return defaultSave(request,brandRepository,brandMapper);
    }

    @Override
    public BrandResponse save(Long aLong, BrandRequest request) {
        return defaultSave(aLong,request,brandRepository,brandMapper,"");
    }

    @Override
    public void delete(Long aLong) {
        brandRepository.deleteById(aLong);

    }

    @Override
    public void delete(List<Long> longs) {
        brandRepository.deleteAllById(longs);

    }

    @Override
    public BrandResponse findByName(String name) {
        return findByName(name);
    }
}
