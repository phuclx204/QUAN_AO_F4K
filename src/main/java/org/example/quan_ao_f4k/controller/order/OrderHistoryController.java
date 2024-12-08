package org.example.quan_ao_f4k.controller.order;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.quan_ao_f4k.dto.request.order.OrderHistoryRequest;
import org.example.quan_ao_f4k.dto.response.orders.OrderHistoryResponse;
import org.example.quan_ao_f4k.mapper.order.OrderDetailMapper;
import org.example.quan_ao_f4k.mapper.order.OrderHistoryMapper;
import org.example.quan_ao_f4k.mapper.product.ProductDetailMapper;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.model.order.OrderHistory;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.order.OrderHistoryRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.service.order.OrderServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/order-history")
@AllArgsConstructor
@Slf4j
public class OrderHistoryController {
    private final OrderDetailRepository orderDetailRepository;
    private OrderServiceImpl orderService;
    private OrderRepository orderRepository;
    private ImageRepository imageRepository;
    private OrderHistoryRepository orderHistoryRepository;
    private ProductDetailRepository productDetailRepository;

    private ProductDetailMapper productDetailMapper;
    private OrderHistoryMapper orderHistoryMapper;
    private OrderDetailMapper orderDetailMapper;


    // Lấy danh sách tất cả lịch sử đơn hàng
    @GetMapping
    public ResponseEntity<List<OrderHistoryResponse>> getAllOrderHistories() {
        log.info("Fetching all order histories");
        List<OrderHistory> orderHistories = orderHistoryRepository.findAll(); // Lấy tất cả lịch sử đơn hàng từ DB
        List<OrderHistoryResponse> orderHistoryDtos = orderHistoryMapper.entityToResponse(orderHistories); // Chuyển thành DTO
        return ResponseEntity.ok(orderHistoryDtos); // Trả về danh sách OrderHistoryResponse
    }

    // Lấy thông tin chi tiết của một lịch sử đơn hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderHistoryResponse> getOrderHistoryById(@PathVariable Long id) {
        log.info("Fetching order history with ID: {}", id);
        return orderHistoryRepository.findById(id)
                .map(orderHistory -> ResponseEntity.ok(orderHistoryMapper.entityToResponse(orderHistory)))
                .orElseGet(() -> {
                    log.error("Order history with ID: {} not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Trả về lỗi 404 nếu không tìm thấy
                });
    }

    // Thêm mới lịch sử đơn hàng
    @PostMapping
    public ResponseEntity<OrderHistoryResponse> createOrderHistory(@RequestBody OrderHistoryRequest orderHistoryRequest) {
        log.info("Creating new order history");
        OrderHistory orderHistory = orderHistoryMapper.requestToEntity(orderHistoryRequest); // Chuyển đổi từ request sang entity
        OrderHistory savedOrderHistory = orderHistoryRepository.save(orderHistory); // Lưu vào DB
        return ResponseEntity.status(HttpStatus.CREATED).body(orderHistoryMapper.entityToResponse(savedOrderHistory)); // Trả về entity đã lưu dưới dạng DTO
    }

    // Cập nhật lịch sử đơn hàng
    @PutMapping("/{id}")
    public ResponseEntity<OrderHistoryResponse> updateOrderHistory(@PathVariable Long id, @RequestBody OrderHistoryRequest orderHistoryRequest) {
        log.info("Updating order history with ID: {}", id);
        return orderHistoryRepository.findById(id)
                .map(existingOrderHistory -> {
                    // Cập nhật entity với dữ liệu từ request
                    OrderHistory updatedOrderHistory = orderHistoryMapper.partialUpdate(existingOrderHistory, orderHistoryRequest);
                    orderHistoryRepository.save(updatedOrderHistory); // Lưu entity đã cập nhật
                    return ResponseEntity.ok(orderHistoryMapper.entityToResponse(updatedOrderHistory)); // Trả về thông tin đã cập nhật
                })
                .orElseGet(() -> {
                    log.error("Order history with ID: {} not found", id);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Trả về lỗi 404 nếu không tìm thấy
                });
    }

    // Xóa lịch sử đơn hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderHistory(@PathVariable Long id) {
        log.info("Deleting order history with ID: {}", id);
        if (orderHistoryRepository.existsById(id)) {
            orderHistoryRepository.deleteById(id); // Xóa lịch sử đơn hàng
            return ResponseEntity.noContent().build(); // Trả về status 204 No Content khi xóa thành công
        } else {
            log.error("Order history with ID: {} not found", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Trả về lỗi 404 nếu không tìm thấy
        }
    }

    @PutMapping("/{orderId}/update-product-detail")
    public ResponseEntity<String> updateProductDetail(@PathVariable Long orderId, @RequestParam boolean check) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (!optionalOrder.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order not found");
        }

        Order order = optionalOrder.get();
        List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailsByOrderId(orderId);

        if (orderDetails.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Order details not found");
        }

        try {
            int quantity;
            if (check) {
                // Chỉ trừ số lượng sản phẩm khi trạng thái đơn hàng là 5
                if (order.getStatus() == 8) {
                    for (OrderDetail orderDetail : orderDetails) {
                        ProductDetail productDetail = orderDetail.getProductDetail();
                        quantity = productDetail.getQuantity() - orderDetail.getQuantity();
                        productDetail.setQuantity(quantity);
                        productDetailRepository.save(productDetail);
                    }
                } else {
                    return ResponseEntity.ok("Product details updated successfully");
                }
            } else {
              if(order.getStatus() == 0){
                    for (OrderDetail orderDetail : orderDetails) {
                        ProductDetail productDetail = orderDetail.getProductDetail();
                        quantity = productDetail.getQuantity() + orderDetail.getQuantity();
                        productDetail.setQuantity(quantity);
                        productDetailRepository.save(productDetail);
                    }
                }
            }

            return ResponseEntity.ok("Product details updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating product details: " + e.getMessage());
        }
    }

}
