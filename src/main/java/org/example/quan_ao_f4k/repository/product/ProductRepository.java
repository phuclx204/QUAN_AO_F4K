package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long>,
		JpaSpecificationExecutor<Product> {
	@Query("SELECT COUNT(p) > 0 FROM Product p " +
			"WHERE LOWER(p.name) = LOWER(:name) " +
			"AND p.brand.id = :brandId " +
			"AND p.category.id = :categoryId " +
			"AND p.id <> :id")
	boolean isUpdateExistProductByBrandAndCate(
			@Param("name") String name,
			@Param("brandId") Long brandId,
			@Param("categoryId") Long categoryId,
			@Param("id") Long id);

	@Query("SELECT COUNT(p) > 0 FROM Product p " +
			"WHERE LOWER(p.name) = LOWER(:name) " +
			"AND p.brand.id = :brandId " +
			"AND p.category.id = :categoryId ")
	boolean isAddExistProductByBrandAndCate(
			@Param("name") String name,
			@Param("brandId") Long brandId,
			@Param("categoryId") Long categoryId);

	@Query("select p from Product p where p.brand.id = :brandId and p.status = 1")
	List<Product> findProductByBrandId(@Param("brandId") Long brandId);

	@Query("SELECT p FROM Product p " +
			"WHERE (:status IS NULL OR p.status = :status) " +
			"AND (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
			"AND (:brandId IS NULL OR p.brand.id = :brandId) " +
			"AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
			"ORDER BY p.id DESC")
	List<Product> getListSearch(@Param("name") String name,
								@Param("status") Integer status,
								@Param("categoryId") Long categoryId,
								@Param("brandId") Long brandId);

	@Query("select p from Product p where p.status = :status")
	List<Product> findProductByStatus(@Param("status") Integer status);
}
