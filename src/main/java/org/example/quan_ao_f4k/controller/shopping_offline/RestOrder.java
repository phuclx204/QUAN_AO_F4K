package org.example.quan_ao_f4k.controller.shopping_offline;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.order.UpdateQuantityRequest;
import org.example.quan_ao_f4k.model.address.District;
import org.example.quan_ao_f4k.model.address.Province;
import org.example.quan_ao_f4k.model.address.Ward;
import org.example.quan_ao_f4k.model.order.OrderProductDetailKey;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.address.AddressServiceImpl;
import org.example.quan_ao_f4k.service.order.OrderDetailService;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;
import org.example.quan_ao_f4k.service.product.ProductDetailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/admin/shopping-offlinee")
@AllArgsConstructor
public class RestOrder {
    private final OrderServiceImpl orderService;
    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final AddressServiceImpl addressService; ;
    private final ProductDetailService productDetailService;

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
    @PutMapping("/{productId}/quantity-plus")
    public ResponseEntity<String> updateQuantity(
            @PathVariable Long productId,
            @RequestBody UpdateQuantityRequest request) {

        try {
            // Gọi service cập nhật số lượng, lấy giá trị từ request DTO
            orderDetailService.updateQuantityPlus(productId, request.getQuantity());
            return ResponseEntity.ok("Cập nhật số lượng thành công cho sản phẩm có ID: " + productId);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{productDetailId}/product-detail-quantity")
    public ResponseEntity<Integer> getQuantity(@PathVariable("productDetailId") Long productDetailId) {
        Integer quantity = productDetailService.getQuantity(productDetailId);
        return ResponseEntity.ok(quantity);
    }

    @GetMapping("/provinces")
    public ResponseEntity<List<Province>> getAllProvinces() {
        return ResponseEntity.ok(addressService.getAllProvinces());
    }

    @GetMapping("/districts/{provinceId}")
    public ResponseEntity<List<District>> getDistrictsByProvince(@PathVariable Long provinceId) {
        try {
            List<District> districts = addressService.getDistrictsByProvince(provinceId);
            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @GetMapping("/wards/{districtId}")
    public ResponseEntity<List<Ward>> getWardsByDistrict(@PathVariable Long districtId) {
        try {
            List<Ward> wards = addressService.getWardsByDistrict(districtId);
            return ResponseEntity.ok(wards);
        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

}

