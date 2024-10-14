package org.example.quan_ao_f4k.service.shop;

import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.model.product.Size;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class ShopService {

    @Autowired
    private CriteriaRepository criteriaRepository;


    public void addModelFilter(Model model) {
        model.addAttribute("listProduct", this.getProductCategory());
        model.addAttribute("listBrand", this.getProductBrand());
        model.addAttribute("listSize", criteriaRepository.findAllByStatus(Size.class));
    }

    private Map<Object, Number> getProductBrand() {
        return getProductMap(Brand.class,
                item -> criteriaRepository
                        .findProductByField(Product.class,"brand", item.getId()).size());
    }
    private Map<Object, Number> getProductCategory() {
        return getProductMap(Product.class,
                item -> criteriaRepository
                        .findProductByField(ProductDetail.class, "product", item.getId()).size());
    }

    private <T> Map<Object, Number> getProductMap(Class<T> clazz, Function<T, Integer> getSizeFunction) {
        List<T> items = criteriaRepository.findAllByStatus(clazz);
        Map<Object, Number> category = new HashMap<>();
        for (T item : items) {
            int size = getSizeFunction.apply(item);
            if (size <= 0) continue;
            category.put(item, size);
        }
        return category;
    }
}
