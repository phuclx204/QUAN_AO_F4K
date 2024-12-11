package org.example.quan_ao_f4k.repository.order;

import org.example.quan_ao_f4k.dto.response.orders.PdfShopOfflineDTO;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, OrderProductDetailKey>,
		JpaSpecificationExecutor<OrderDetail> {

	@Query("SELECT COUNT(od) FROM OrderDetail od WHERE od.productDetail.id = :isParent")
	int countByProductDetail(@Param("isParent") Long isParent);

	@Query("SELECT o FROM OrderDetail o WHERE o.order.id = :orderId")
	List<OrderDetail> findOrderDetailsByOrderId(@Param("orderId") Long orderId);

	@Query("SELECT EXISTS(SELECT 1 FROM OrderDetail o WHERE o.productDetail.product.id = ?1)")
	boolean existsByProductDetailId(Long productDetailId);

	@Query("SELECT o FROM OrderDetail o WHERE o.order.id = :orderId and o.productDetail.id = :productDetailId")
	OrderDetail findByProductDetailIdAndOrderId(@Param("orderId") Long orderId, @Param("productDetailId") Long productDetailId);

	// sonng - shop site
	@Query("SELECT o from OrderDetail o where o.order.user.id = :userId and (:status is null or o.order.status = :status) order by o.order.id desc ")
	List<OrderDetail> findAllByOrderUserIdAndOrderStatus(@Param("userId") Long userId, @Param("status") Integer status);

	@Query("SELECT o from OrderDetail o where(:userId is null or  o.order.user.id = :userId) and o.order.id = :orderId order by o.order.id desc ")
	List<OrderDetail> findAllByOrderIdAndUserId(@Param("userId") Long userId, @Param("orderId") Long orderId);

	List<OrderDetail> findAllByOrder_Id(Long orderId);

}
