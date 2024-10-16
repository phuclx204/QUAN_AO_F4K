package org.example.quan_ao_f4k.repository.shop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.example.quan_ao_f4k.dto.request.shop.ShopRequest;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Repository
public class CriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Product> searchProductByRequest(ShopRequest.RequestSearch objSearch) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<ProductDetail> productDetail = query.from(ProductDetail.class); // Bắt đầu từ ProductDetail

        // Join với Product để lấy thông tin sản phẩm
        Join<ProductDetail, Product> product = productDetail.join("product");

        // = where status = 1
        Predicate predicate = cb.equal(product.get("status"), 1);

        if (!StringUtils.isBlank(objSearch.getName())) {
            // = and name like % ? %
            predicate = cb.and(predicate, cb.like(product.get("name"),  "%" + objSearch.getName() + "%"));
        }
        if (!CollectionUtils.isEmpty(objSearch.getBrand())) {
            // = and brand in (?,?,..)
            predicate = cb.and(predicate, product.get("brand").get("id").in(objSearch.getBrand()));
        }
        if (!CollectionUtils.isEmpty(objSearch.getCategory())) {
            // = and category in (?,?,..)
            predicate = cb.and(predicate, product.get("category").get("id").in(objSearch.getCategory()));
        }
        if (!CollectionUtils.isEmpty(objSearch.getSize())) {
            // = and size in (?,?,..)
            predicate = cb.and(predicate, productDetail.get("size").get("id").in(objSearch.getSize()));
        }
        if (!CollectionUtils.isEmpty(objSearch.getColor())) {
            // = and color in (?,?,..)
            predicate = cb.and(predicate, productDetail.get("color").get("id").in(objSearch.getColor()));
        }

        if (!CollectionUtils.isEmpty(objSearch.getColor())) {
            // = and color in (?,?,..)
            predicate = cb.and(predicate, productDetail.get("color").get("id").in(objSearch.getColor()));
        }

        if (objSearch.getPriceTo() != null && objSearch.getPriceForm() != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(productDetail.get("price"), objSearch.getPriceForm()));
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(productDetail.get("price"), objSearch.getPriceTo()));
        }

        query.select(product).where(predicate);
        if (objSearch.getOrderBy().equals("desc")) {
            query.orderBy(cb.desc(productDetail.get("price")));
        } else {
            query.orderBy(cb.asc(productDetail.get("price")));
        }

        return entityManager.createQuery(query).getResultList();
    }

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


    // not touch
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
