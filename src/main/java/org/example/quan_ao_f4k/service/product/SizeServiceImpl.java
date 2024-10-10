package org.example.quan_ao_f4k.service.product;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.SizeRequest;
import org.example.quan_ao_f4k.dto.response.product.SizeResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.SizeMapper;
import org.example.quan_ao_f4k.model.product.Size;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.SizeRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SizeServiceImpl implements SizeService{
    private SizeMapper sizeMapper;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ProductDetailRepository productDetailRepository;

    @Override
    public ListResponse<SizeResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.COLOR, sizeRepository, sizeMapper);
    }

    @Override
    public SizeResponse findById(Long aLong) {
        return findById(aLong);
    }

    @Override
    public SizeResponse save(SizeRequest request) {
        request.setStatus(0);
        if (sizeRepository.findByName(request.getName()) != null) {
            throw new BadRequestException(
                    F4KConstants.ErrCode.IS_EXITS.getDescription(),
                    request.getName(),
                    F4KConstants.TableCode.SIZE
            );
        }
        return defaultSave(request, sizeRepository, sizeMapper);
    }

    @Override
    public SizeResponse save(Long aLong, SizeRequest request) {
        request.setStatus(0);
        sizeRepository.findById(aLong).orElseThrow(
                () -> new BadRequestException(
                        F4KConstants.ErrCode.NOT_FOUND.getDescription()
                        , aLong
                        , F4KConstants.TableCode.SIZE)
        );
        Size objTmp = sizeRepository.findByName(request.getName());
        if (objTmp == null) {
            return defaultSave(aLong, request, sizeRepository, sizeMapper, "");
        } else {
            if (Objects.equals(objTmp.getId(), aLong) && objTmp.getName().equals(request.getName())) {
                return defaultSave(aLong, request, sizeRepository, sizeMapper, "");
            } else {
                throw new BadRequestException(
                        F4KConstants.ErrCode.IS_EXITS.getDescription(),
                        request.getName(),
                        F4KConstants.TableCode.SIZE
                );
            }
        }
    }

    @Override
    public void delete(Long aLong) {
        if (!productDetailRepository.findBySizeId(aLong).isEmpty()) {
            throw new BadRequestException("Không xóa được dữ liệu đang được sử dụng");
        }
        sizeRepository.deleteById(aLong);
    }

    @Override
    public void delete(List<Long> longs) {
        sizeRepository.deleteAllById(longs);

    }
}
