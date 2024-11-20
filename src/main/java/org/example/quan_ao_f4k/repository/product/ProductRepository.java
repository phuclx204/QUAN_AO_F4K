package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

	@Query("select p from Product p inner join ProductDetail pd on pd.product.id = p.id where p.brand.id = :brandId and p.status = 1")
	List<Product> findProductByBrandId(@Param("brandId") Long brandId);
}
