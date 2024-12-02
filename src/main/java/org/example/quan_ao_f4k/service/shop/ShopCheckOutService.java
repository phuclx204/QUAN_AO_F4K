package org.example.quan_ao_f4k.service.shop;

import org.example.quan_ao_f4k.model.authentication.User;
import org.example.quan_ao_f4k.model.order.Order;
import org.example.quan_ao_f4k.model.order.OrderDetail;
import org.example.quan_ao_f4k.util.HoaDonUtils;
import org.springframework.ui.Model;

import java.util.List;

public interface ShopCheckOutService {
    void addModelCheckout(Model model);

    Order createOrder(HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang);
    Order createOrder(HoaDonUtils.PhuongThucMuaHang phuongThucMuaHang, boolean isClear);

    void cancelOrder(Long orderId, String note);

    public void clearCart(User user);

    List<OrderDetail> getOrderDetailByOrder(Order order);

    void addModalPurchaseHistory(Model model);
}