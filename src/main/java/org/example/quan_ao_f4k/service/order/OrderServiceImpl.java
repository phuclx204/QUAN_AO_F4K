package org.example.quan_ao_f4k.service.order;

import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.order.OrderRequest;
import org.example.quan_ao_f4k.dto.request.order.OrderResponse;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.address.WardMapper;
import org.example.quan_ao_f4k.mapper.order.OrderMapper;
import org.example.quan_ao_f4k.repository.address.WardRepository;
import org.example.quan_ao_f4k.repository.order.OrderRepository;
import org.example.quan_ao_f4k.service.product.ProductServiceImpl;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService{
    private OrderMapper orderMapper;
    private OrderRepository orderRepository;
    private ProductServiceImpl productService;
    @Override
    public ListResponse<OrderResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page,size,sort,filter,search,all, SearchFields.ORDER,orderRepository,orderMapper);
    }

    @Override
    public OrderResponse findById(Long aLong) {
        return null;
    }

    @Override
    public OrderResponse save(OrderRequest request) {
        return defaultSave(request, orderRepository, orderMapper);
    }

    @Override
    public OrderResponse save(Long aLong, OrderRequest request) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(List<Long> longs) {

    }
    public List<OrderResponse> findOrdersByStatus(int status) {
        return orderRepository.findOrdersByStatus(status)
                .stream()
                .map(orderMapper::entityToResponse)
                .collect(Collectors.toList());
    }
    public void addModelOrder(Model model) {
        model.addAttribute("listOrder",this.findOrdersByStatus(5));
    }
}
