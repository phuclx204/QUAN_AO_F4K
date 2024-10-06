package org.example.quan_ao_f4k.service.product;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.SizeRequest;
import org.example.quan_ao_f4k.dto.response.product.SizeResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.SizeMapper;
import org.example.quan_ao_f4k.repository.product.SizeRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class SizeServiceImpl implements SizeService{
    private SizeMapper sizeMapper;
    private SizeRepository sizeRepository;

    @Override
    public ListResponse<SizeResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page,size,sort,filter,search,all, SearchFields.SIZE,sizeRepository,sizeMapper);

    }

    @Override
    public SizeResponse findById(Long aLong) {
        return findById(aLong);
    }

    @Override
    public SizeResponse save(SizeRequest request) {
        return defaultSave(request,sizeRepository,sizeMapper);
    }

    @Override
    public SizeResponse save(Long aLong, SizeRequest request) {
        return defaultSave(aLong,request,sizeRepository,sizeMapper,"");
    }

    @Override
    public void delete(Long aLong) {
        sizeRepository.deleteById(aLong);

    }

    @Override
    public void delete(List<Long> longs) {
        sizeRepository.deleteAllById(longs);

    }
}
