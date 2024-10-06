package org.example.quan_ao_f4k.service.product;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.ColorRequest;
import org.example.quan_ao_f4k.dto.response.product.ColorResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.ColorMapper;
import org.example.quan_ao_f4k.repository.product.ColorRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ColorServiceImpl implements ColorService{
    private ColorMapper colorMapper;
    private ColorRepository colorRepository;

    @Override
    public ListResponse<ColorResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page,size,sort,filter,search,all, SearchFields.COLOR,colorRepository,colorMapper);

    }

    @Override
    public ColorResponse findById(Long aLong) {
        return findById(aLong);
    }

    @Override
    public ColorResponse save(ColorRequest request) {
        return defaultSave(request,colorRepository,colorMapper);
    }

    @Override
    public ColorResponse save(Long aLong, ColorRequest request) {
        return defaultSave(aLong,request,colorRepository,colorMapper,"");
    }

    @Override
    public void delete(Long aLong) {
        colorRepository.deleteById(aLong);

    }

    @Override
    public void delete(List<Long> longs) {
        colorRepository.deleteAllById(longs);

    }
}
