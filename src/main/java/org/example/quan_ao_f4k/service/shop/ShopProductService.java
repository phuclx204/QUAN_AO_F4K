package org.example.quan_ao_f4k.service.shop;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.quan_ao_f4k.dto.request.shop.ShopRequest;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.JacksonEx;
import org.example.quan_ao_f4k.util.SimpleEncoderDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class ShopProductService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    public Page<ObjectNode> getListProductDetail(ShopRequest.RequestSearch requestSearch) {
        List<Product> productList = criteriaRepository.searchProductByRequest(requestSearch);

        List<ObjectNode> listNodes = new ArrayList<>();
        for (Product product : productList) {
            ProductDetail productDetail = criteriaRepository.getFirstProductDetailById(product.getId());
            if (productDetail == null) continue;

            ObjectNode nodes = JacksonEx.convertObject2Node(productDetail);
            nodes.put("id", SimpleEncoderDecoder.encode(productDetail.getId() + ""));

            ArrayNode nodeImages = JacksonEx.convertList2ArrayNode(criteriaRepository.getImgByProductDetailId(productDetail.getId()));
            nodes.put("listImage", nodeImages);

            listNodes.add(nodes);
        }

        Pageable pageable = PageRequest.of(requestSearch.getPage(), requestSearch.getPageSize());
        return F4KUtils.toPage(listNodes, pageable);
    }

    public void addModelFilter(Model model) {
        model.addAttribute("listProduct", this.getProductCategory());
        model.addAttribute("listBrand", this.getProductBrand());
        model.addAttribute("listSize", criteriaRepository.findAllByStatus(Size.class));
        model.addAttribute("listColor", criteriaRepository.findAllByStatus(Color.class));
        model.addAttribute("listCategory", criteriaRepository.findAllByStatus(Category.class));
    }

    private Map<Object, Number> getProductBrand() {
        return getProductMap(Brand.class,
                item -> criteriaRepository
                        .findProductByField(Product.class, "brand", item.getId()).size());
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
