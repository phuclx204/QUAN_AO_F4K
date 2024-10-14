package org.example.quan_ao_f4k.service.shop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.StoredProcedureQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SpBaseImpl implements SpBase {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <T> T executeObjectUsingSp(String spName, Object[] params, Class<T> resultClass) {
        try {
            StoredProcedureQuery query = createStoredProcedureQuery(spName, params, resultClass);
            query.execute();

            return getSingleResult(query);
        } catch (Exception e) {
            throw new RuntimeException("Error executing stored procedure: " + spName, e);
        }
    }

    @Override
    public <T> List<T> executeListUsingSp(String spName, Object[] params, Class<T> resultClass) {
        try {
            StoredProcedureQuery query = createStoredProcedureQuery(spName, params, resultClass);
            query.execute();

            return query.getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Error executing stored procedure: " + spName, e);
        }
    }

    private <T> StoredProcedureQuery createStoredProcedureQuery(String spName, Object[] params, Class<T> resultClass) {
        StoredProcedureQuery query = entityManager.createStoredProcedureQuery(spName, resultClass);

        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                query.registerStoredProcedureParameter(i + 1, params[i].getClass(), ParameterMode.IN);
                query.setParameter(i + 1, params[i]);
            }
        }

        return query;
    }

    private <T> T getSingleResult(StoredProcedureQuery query) {
        List<T> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}