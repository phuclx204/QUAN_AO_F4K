package org.example.quan_ao_f4k.service.shop;

import java.util.List;

public interface SpBase {
    /**
     * Execute stored procedure and return a single object.
     *
     * @param spName The name of the stored procedure
     * @param params The parameters to pass to the stored procedure
     * @param resultClass The class of the result object
     * @param <T> The type of the result object
     * @return The result object of type T
     */
    <T> T executeObjectUsingSp(String spName, Object[] params, Class<T> resultClass);

    /**
     * Execute stored procedure and return a list of objects.
     *
     * @param spName The name of the stored procedure
     * @param params The parameters to pass to the stored procedure
     * @param resultClass The class of the result object
     * @param <T> The type of the result object
     * @return The result list of objects of type T
     */
    <T> List<T> executeListUsingSp(String spName, Object[] params, Class<T> resultClass);
}