package org.example.quan_ao_f4k.service.product;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.CategoryMapper;
import org.example.quan_ao_f4k.repository.product.CategoryRepository;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService{
	private CategoryMapper categoryMapper;
	private CategoryRepository categoryRepository;


	@Override
	public ListResponse<CategoryResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
		return defaultFindAll(page,size,sort,filter,search,all, SearchFields.CATEGORY,categoryRepository,categoryMapper);
	}

	@Override
	public CategoryResponse findById(Long aLong) {
		return findById(aLong);
	}

	@Override
	public CategoryResponse save(CategoryRequest request) {
		return defaultSave(request,categoryRepository,categoryMapper);
	}

	@Override
	public CategoryResponse save(Long aLong, CategoryRequest request) {
		return defaultSave(aLong,request,categoryRepository,categoryMapper,"");

	}

	@Override
	public void delete(Long aLong) {
		categoryRepository.deleteById(aLong);
	}

	@Override
	public void delete(List<Long> longs) {
		categoryRepository.deleteAllById(longs);
	}
}
