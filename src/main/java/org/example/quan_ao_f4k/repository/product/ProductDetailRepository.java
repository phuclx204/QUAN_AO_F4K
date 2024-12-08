package org.example.quan_ao_f4k.repository.product;

import org.example.quan_ao_f4k.dto.request.shop.ShopProductRequest;
import org.example.quan_ao_f4k.dto.request.shop.ShopRequest;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

	// ==== sonng - shop site - start ====
	@Query("SELECT pd FROM ProductDetail pd " +
			"WHERE pd.id = (" +
			"   SELECT MAX(pdi.id) FROM ProductDetail pdi WHERE pdi.product.id = pd.product.id" +
			") " +
			"AND pd.product.status = 1 " +
			"AND (:name IS NULL OR LOWER(pd.product.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
			"AND (:brandIds IS NULL OR pd.product.brand.id IN :brandIds) " +
			"AND (:categoryIds IS NULL OR pd.product.category.id IN :categoryIds) " +
			"AND (:sizeIds IS NULL OR pd.size.id IN :sizeIds) " +
			"AND (:colorIds IS NULL OR pd.color.id IN :colorIds) " +
			"AND (:priceFrom IS NULL OR pd.price >= :priceFrom) " +
			"AND (:priceTo IS NULL OR pd.price <= :priceTo) " +
			"ORDER BY CASE WHEN :orderBy = 'desc' THEN pd.price END DESC, " +
			"CASE WHEN :orderBy = 'asc' THEN pd.price END ASC")
	List<ProductDetail> getListSearch(
			@Param("name") String name,
			@Param("brandIds") List<Long> brandIds,
			@Param("categoryIds") List<Long> categoryIds,
			@Param("sizeIds") List<Long> sizeIds,
			@Param("colorIds") List<Long> colorIds,
			@Param("priceFrom") BigDecimal priceFrom,
			@Param("priceTo") BigDecimal priceTo,
			@Param("orderBy") String orderBy
	);

	//	product detail shopping-offline
	@Query("SELECT pd FROM ProductDetail pd " +
			"WHERE pd.product.status = 1 " +
			"AND pd.status =1" +
			"AND (:name IS NULL OR LOWER(pd.product.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
			"AND (:brandIds IS NULL OR pd.product.brand.id IN :brandIds) " +
			"AND (:categoryIds IS NULL OR pd.product.category.id IN :categoryIds) " +
			"AND (:sizeIds IS NULL OR pd.size.id IN :sizeIds) " +
			"AND (:colorIds IS NULL OR pd.color.id IN :colorIds) " +
			"AND (:priceFrom IS NULL OR pd.price >= :priceFrom) " +
			"AND (:priceTo IS NULL OR pd.price <= :priceTo) " +
			"ORDER BY CASE WHEN :orderBy = 'desc' THEN pd.price END DESC, " +
			"CASE WHEN :orderBy = 'asc' THEN pd.price END ASC")
	List<ProductDetail> getListProductDetailSearch(
			@Param("name") String name,
			@Param("brandIds") List<Long> brandIds,
			@Param("categoryIds") List<Long> categoryIds,
			@Param("sizeIds") List<Long> sizeIds,
			@Param("colorIds") List<Long> colorIds,
			@Param("priceFrom") BigDecimal priceFrom,
			@Param("priceTo") BigDecimal priceTo,
			@Param("orderBy") String orderBy
	);

	@Query("SELECT pd FROM ProductDetail pd " +
			"WHERE pd.product.status = 1 " +
			"AND pd.status =1" +
			"AND (:name IS NULL OR LOWER(pd.product.name) LIKE LOWER(CONCAT('%', :name, '%'))) " +
			"AND (:brandId IS NULL OR pd.product.brand.id = :brandId) " +
			"AND (:categoryId IS NULL OR pd.product.category.id = :categoryId) " +
			"AND (:sizeId IS NULL OR pd.size.id = :sizeId) " +
			"AND (:colorId IS NULL OR pd.color.id = :colorId) " +
			"AND (:priceFrom IS NULL OR pd.price >= :priceFrom) " +
			"AND (:priceTo IS NULL OR pd.price <= :priceTo) " +
			"ORDER BY CASE WHEN :orderBy = 'desc' THEN pd.price END DESC, " +
			"CASE WHEN :orderBy = 'asc' THEN pd.price END ASC")
	List<ProductDetail> searchProductDetail(
			@Param("name") String name,
			@Param("brandId") Long brandId,
			@Param("categoryId") Long categoryId,
			@Param("sizeId") Long sizeId,
			@Param("colorId") Long colorId,
			@Param("priceFrom") BigDecimal priceFrom,
			@Param("priceTo") BigDecimal priceTo,
			@Param("orderBy") String orderBy
	);


	@Query("SELECT p FROM ProductDetail p " +
			"Where p.product.slug = :slug " +
			"AND (:colorHex IS NULL OR p.color.hex = :colorHex)" +
			"AND (:sizeName IS NULL OR LOWER(p.size.name) = LOWER(:sizeName))" +
			"order by p.id desc limit 1")
	Optional<ProductDetail> findProductDetailBySlugProduct(@Param("slug") String slug, @Param("colorHex") String colorHex, @Param("sizeName") String sizeName);

	@Query("SELECT p FROM ProductDetail p " +
			"Where p.product.slug = :slug " +
			"AND (:colorHex IS NULL OR p.color.hex = :colorHex)"
	)
	List<ProductDetail> findProductDetailBySlugProduct(@Param("slug") String slug, @Param("colorHex") String colorHex);

	@Query("SELECT p FROM ProductDetail p " +
			"Where p.product.slug = :slug " +
			"AND p.status = :status"
	)
	List<ProductDetail> findProductDetailBySlugAndStatus(@Param("slug") String slug, @Param("status") Integer status);

	@Query("SELECT pd FROM ProductDetail pd " +
			"LEFT JOIN PromotionProduct prd ON prd.productDetail.id = pd.id " +
			"WHERE pd.id = (" +
			"   SELECT MAX(pdi.id) FROM ProductDetail pdi WHERE pdi.product.id = pd.product.id" +
			") " +
			"AND prd.status = 1" +
			"AND (:promotionId IS NULL OR prd.promotion.id = :promotionId) " +
			"ORDER BY pd.price DESC")
	List<ProductDetail> getListByPromotionId(
			@Param("promotionId") Long promotionId
	);
	// ==== sonng - shop site - end ====

	@Query("SELECT p.quantity FROM ProductDetail p WHERE p.id = :productDetailId")
	Integer findQuantityByProductDetailId(@Param("productDetailId") Long productDetailId);

}
