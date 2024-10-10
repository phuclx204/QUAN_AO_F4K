package org.example.quan_ao_f4k.service.product;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.CategoryMapper;
import org.example.quan_ao_f4k.repository.product.CategoryRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private CategoryMapper categoryMapper;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;


    @Override
    public ListResponse<CategoryResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.CATEGORY, categoryRepository, categoryMapper);
    }

    @Override
    public CategoryResponse findById(Long aLong) {
        return findById(aLong);
    }

    @Override
    public CategoryResponse save(CategoryRequest request) {
        request.setStatus(0);
        if (categoryRepository.findByName(request.getName()) != null) {
            throw new BadRequestException(
                    F4KConstants.ErrCode.IS_EXITS.getDescription(),
                    request.getName(),
                    F4KConstants.TableCode.CATEGORY
            );
        }
        return defaultSave(request, categoryRepository, categoryMapper);
    }

    @Override
    public CategoryResponse save(Long aLong, CategoryRequest request) {
        request.setStatus(0);
        categoryRepository.findById(aLong).orElseThrow(
                () -> new BadRequestException(String.format(
                        F4KConstants.ErrCode.NOT_FOUND.getDescription()
                        , aLong, F4KConstants.TableCode.CATEGORY
                ))
        );
        return defaultSave(aLong, request, categoryRepository, categoryMapper, "");
    }

    @Override
    public void delete(Long aLong) {
        if (!productRepository.findByCategoryId(aLong).isEmpty()) {
            throw new BadRequestException("Không xóa được dữ liệu đang được sử dụng");
        }
        categoryRepository.deleteById(aLong);
    }

    @Override
    public void delete(List<Long> longs) {
        categoryRepository.deleteAllById(longs);
    }
}
