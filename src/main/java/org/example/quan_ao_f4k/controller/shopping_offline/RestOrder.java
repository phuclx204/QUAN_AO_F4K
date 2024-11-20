package org.example.quan_ao_f4k.controller.shopping_offline;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.order.UpdateQuantityRequest;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.order.OrderDetailServiceimpl;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/shopping-offlinee")
@AllArgsConstructor
public class RestOrder {
    private final OrderServiceImpl orderService;
    private final OrderRepository orderRepository;
    private final OrderDetailServiceimpl orderDetailService;
    @DeleteMapping("/order-detail/delete")
    public ResponseEntity<?> deleteOrderDetail(@RequestBody OrderProductDetailKey orderProductDetailKey) {
        System.out.println("Received: " + orderProductDetailKey); // Ghi log dữ liệu nhận
        orderDetailService.delete(orderProductDetailKey);
        return ResponseEntity.ok("Chi tiết đơn hàng đã được xóa thành công");
    }
    @PutMapping("/{productId}/quantity")
    public ResponseEntity<String> updateProductQuantity(
            @PathVariable Long productId,
            @RequestBody UpdateQuantityRequest request) { // Chuyển từ @RequestParam sang @RequestBody

        try {
            // Gọi service cập nhật số lượng, lấy giá trị từ request DTO
            orderDetailService.updateQuantity(productId, request.getQuantity());
            return ResponseEntity.ok("Cập nhật số lượng thành công cho sản phẩm có ID: " + productId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



}
