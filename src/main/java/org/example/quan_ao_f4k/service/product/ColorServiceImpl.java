package org.example.quan_ao_f4k.service.product;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.ColorRequest;
import org.example.quan_ao_f4k.dto.response.product.ColorResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.ColorMapper;
import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.repository.product.ColorRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ColorServiceImpl implements ColorService {
    private ColorMapper colorMapper;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Override
    public ListResponse<ColorResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.COLOR, colorRepository, colorMapper);
    }

    @Override
    public ColorResponse findById(Long aLong) {
        return findById(aLong);
    }

    @Override
    public ColorResponse save(ColorRequest request) {
        request.setStatus(0);
        if (colorRepository.findByName(request.getName()) != null) {
            throw new BadRequestException(
                    F4KConstants.ErrCode.IS_EXITS.getDescription(),
                    request.getName(),
                    F4KConstants.TableCode.COLOR
            );
        }
        return defaultSave(request, colorRepository, colorMapper);
    }

    @Override
    public ColorResponse save(Long aLong, ColorRequest request) {
        request.setStatus(0);
        colorRepository.findById(aLong).orElseThrow(
                () -> new BadRequestException(
                        F4KConstants.ErrCode.NOT_FOUND.getDescription()
                        , aLong
                        , F4KConstants.TableCode.COLOR)
        );
        Color objTmp = colorRepository.findByName(request.getName());
        if (objTmp == null) {
            return defaultSave(aLong, request, colorRepository, colorMapper, "");
        } else {
            if (Objects.equals(objTmp.getId(), aLong) && objTmp.getName().equals(request.getName())) {
                return defaultSave(aLong, request, colorRepository, colorMapper, "");
            } else {
                throw new BadRequestException(
                        F4KConstants.ErrCode.IS_EXITS.getDescription(),
                        request.getName(),
                        F4KConstants.TableCode.COLOR
                );
            }
        }
    }

    @Override
    public void delete(Long aLong) {
        if (!productDetailRepository.findByColorId(aLong).isEmpty()) {
            throw new BadRequestException("Không xóa được dữ liệu đang được sử dụng");
        }
        colorRepository.deleteById(aLong);
    }

    @Override
    public void delete(List<Long> longs) {
        colorRepository.deleteAllById(longs);

    }
}
