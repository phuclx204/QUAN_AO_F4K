package org.example.quan_ao_f4k.repository.shop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class CriteriaRepository {

    @PersistenceContext
    private EntityManager entityManager;

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
                        cb.equal(object.get("status"), 1));

        return entityManager.createQuery(query).getResultList();
    }

    public <T> List<T> findAllByStatus(Class<T> clazz) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(clazz);
        Root<T> object = query.from(clazz);

        query.select(object)
                .where(cb.equal(object.get("status"), 1));

        return entityManager.createQuery(query).getResultList();
    }
}
