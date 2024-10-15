package org.example.quan_ao_f4k.repository.shop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

@Repository
public class CriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ProductDetail> getProductDetailsByIdParent(Long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProductDetail> query = builder.createQuery(ProductDetail.class);
        Root<ProductDetail> object = query.from(ProductDetail.class);

        query.select(object)
                .where(builder.equal(object.get("product").get("id"), id));

        return entityManager.createQuery(query).getResultList();
    }

    public List<Image> getImgByProductDetailId(Long id) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Image> query = builder.createQuery(Image.class);
        Root<Image> object = query.from(Image.class);

        query.select(object)
                .where(builder.equal(object.get("idParent"), id));

        return entityManager.createQuery(query).getResultList();
    }

    public <T> List<T> findProductByField(Class<T> clazz, String fieldName, Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> object = query.from(clazz);

        // Kiểm tra tham số
        if (fieldName == null || id == null) {
            return Collections.emptyList();
        }

        // = select p from p product detail where p.filename = id and status = 1
        query.select(object)
                .where(cb.equal(object.get(fieldName).get("id"), id),
                        cb.equal(object.get("status"), F4KConstants.STATUS_ON));

        return entityManager.createQuery(query).getResultList();
    }

    public <T> List<T> findAllByStatus(Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> object = query.from(clazz);

        query.select(object)
                .where(cb.equal(object.get("status"), 1));

        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        return pagination(typedQuery, 1, 30);
    }

    public <T> List<T> pagination(TypedQuery<T> query, int pageNumber, int pageSize) {
        int firstResult = (pageNumber - 1) * pageSize;
        query.setFirstResult(firstResult);
        query.setMaxResults(pageSize);

        return query.getResultList();
    }

    public <T> Page<T> searchAndPaginate(Class<T> clazz, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(clazz);
        Root<T> root = cq.from(clazz);

        // Add your search conditions here if any
        cq.select(root);

        // Fetch total count for pagination
        TypedQuery<T> countQuery = entityManager.createQuery(cq);
        int totalElements = countQuery.getResultList().size();

        // Apply pagination using Pageable
        TypedQuery<T> pagedQuery = entityManager.createQuery(cq)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        // Fetch paginated content
        List<T> content = pagedQuery.getResultList();

        // Return as a Page<Object>
        return new PageImpl<>(content, pageable, totalElements);
    }
}
