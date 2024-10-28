package org.example.quan_ao_f4k.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.perplexhub.rsql.RSQLJPASupport;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.GennericMapper;
import org.example.quan_ao_f4k.util.SearchUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;

public interface CrudService<ID, I, O> {


    ListResponse<O> findAll(int page, int size, String sort, String filter, String search, boolean all);

    O findById(ID id);

    O save(I request);

    O save(ID id, I request);

    void delete(ID id);

    void delete(List<ID> ids);



    default <E> ListResponse<O> defaultFindAll(int page, int size,
                                               String sort, String filter,
                                               String search, boolean all,
                                               List<String> searchFields,
                                               JpaSpecificationExecutor<E> repository,
                                               GennericMapper<E, I, O> mapper) {
        Specification<E> sortable = RSQLJPASupport.toSort(sort);
        Specification<E> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<E> searchable = SearchUtils.parse(search, searchFields);
        Specification<E> statusFilter = extractStatusFilter(filter);

        LocalDate startDate = extractDateFromFilter(filter, "startDate");
        LocalDate endDate = extractDateFromFilter(filter, "endDate");

        Specification<E> dateRangeSpec = (root, query, criteriaBuilder) -> {
            if (startDate != null && endDate != null) {
                return criteriaBuilder.between(root.get("created_at"), startDate, endDate);
            } else if (startDate != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("created_at"), startDate);
            } else if (endDate != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("created_at"), endDate);
            }
            return criteriaBuilder.conjunction(); // Không áp dụng bộ lọc nếu không có ngày
        };

        // Kết hợp các tiêu chí lọc thành một Specification
        Specification<E> finalSpec = sortable.and(filterable).and(searchable).and(statusFilter).and(dateRangeSpec);

        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);
        Page<E> entities = repository.findAll(finalSpec, pageable);
        List<O> entityResponses = mapper.entityToResponse(entities.getContent());
        return new ListResponse<>(entityResponses, entities);
    }

    default <E> O defaultSave(I request,
                              JpaRepository<E, ID> repository,
                              GennericMapper<E, I, O> mapper) {
        E entity = mapper.requestToEntity(request);
        entity = repository.save(entity);
        return mapper.entityToResponse(entity);
    }

    default <E> O defaultSave(ID id, I request,
                              JpaRepository<E, ID> repository,
                              GennericMapper<E, I, O> mapper,
                              String resourceName) {

        return repository.findById(id)
                .map(existingEntity -> mapper.partialUpdate(existingEntity, request))
                .map(repository::save)
                .map(mapper::entityToResponse)
                .orElseThrow(() -> new RuntimeException(""));
    }


    default <E> O defaultFindById(ID id,
                                  JpaRepository<E, ID> repository,
                                  GennericMapper<E, I, O> mapper,
                                  String resourceName) {
        return repository.findById(id)
                .map(mapper::entityToResponse)
                .orElseThrow(() -> new RuntimeException("Loi defaufinall"));
    }

    default O save(JsonNode request, Class<I> requestType) {
        ObjectMapper mapper = new ObjectMapper();
        I typedRequest = mapper.convertValue(request, requestType);
        return save(typedRequest);
    }

    default O save(ID id, JsonNode request, Class<I> requestType) {
        ObjectMapper mapper = new ObjectMapper();
        I typedRequest = mapper.convertValue(request, requestType);
        return save(id, typedRequest);
    }

    default  <E, I, O> ListResponse<O> defaultFindDetailsByProductId(
            Long productId, int page, int size,
            String sort, String filter, String search, boolean all,
            List<String> searchFields,
            JpaSpecificationExecutor<E> repository,
            GennericMapper<E, I, O> mapper,
            String resourceName) {

        Specification<E> byProductId = (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("product").get("id"), productId);

        Specification<E> filterable = RSQLJPASupport.toSpecification(filter);
        Specification<E> searchable = SearchUtils.parse(search, searchFields);

        Specification<E> spec = Specification.where(byProductId)
                .and(filterable != null ? filterable : null)
                .and(searchable != null ? searchable : null);

        Pageable pageable = all ? Pageable.unpaged() : PageRequest.of(page - 1, size);

        Page<E> entities = repository.findAll(spec, pageable);

        List<O> responseList = mapper.entityToResponse(entities.getContent());

        return new ListResponse<>(responseList, entities);
    }

    // Hàm hỗ trợ: Trích xuất bộ lọc status từ filter
    private <E> Specification<E> extractStatusFilter(String filter) {
        if (filter != null && filter.contains("status==")) {
            // Trích xuất giá trị status từ chuỗi filter
            String[] parts = filter.split(";");
            for (String part : parts) {
                if (part.startsWith("status==")) {
                    String statusValue = part.split("==")[1];
                    return (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("status"), Integer.parseInt(statusValue));
                }
            }
        }
        // Trả về Specification mặc định nếu không có status trong filter
        return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
    }

    // Phương thức phụ để lấy ngày từ filter
    private LocalDate extractDateFromFilter(String filter, String key) {
        if (filter != null && filter.contains(key + "=")) {
            try {
                String[] parts = filter.split(key + "=");
                String dateString = parts[1].split("&")[0];
                return LocalDate.parse(dateString); // Cần đảm bảo định dạng đúng ISO (yyyy-MM-dd)
            } catch (Exception e) {
                e.printStackTrace(); // Xử lý ngoại lệ nếu cần
            }
        }
        return null;
    }

}