package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Long>,
		JpaSpecificationExecutor<ProductDetail> {

	@Query("SELECT COUNT(pd) > 0 FROM ProductDetail pd " +
			"WHERE pd.product.id = :productId " +
			"AND pd.size.id = :sizeId " +
			"AND pd.color.id = :colorId")
	boolean isAddExistsByProductSizeAndColor(
			@Param("productId") Long productId,
			@Param("sizeId") Long sizeId,
			@Param("colorId") Long colorId);

	@Query("SELECT COUNT(pd) > 0 FROM ProductDetail pd " +
			"WHERE pd.product.id = :productId " +
			"AND pd.size.id = :sizeId " +
			"AND pd.color.id = :colorId " +
			"AND pd.id <> :id")
	boolean isUpdateExistsByProductSizeAndColorId(
			@Param("productId") Long productId,
			@Param("sizeId") Long sizeId,
			@Param("colorId") Long colorId,
			@Param("id") Long id);

	@Query("SELECT pd FROM ProductDetail pd WHERE pd.product.id = :productId")
	List<ProductDetail> findProductDetailsByProductId(@Param("productId") Long productId);

	@Query("SELECT pd FROM ProductDetail pd WHERE pd.product.id = ?1 and pd.color.name = ?2 order by pd.id asc limit 1")
	Optional<ProductDetail> findProductDetailsByProductIdAndSizeAndColorId(Long id, String color);

	void deleteAllByProductId(Long productId);
}
