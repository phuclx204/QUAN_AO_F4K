package org.example.quan_ao_f4k.service.shop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.example.quan_ao_f4k.mapper.product.ProductMapper;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.JacksonEx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ShopService {

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private ProductMapper productMapper;

    public void addModelFilter(Model model) {
        model.addAttribute("listProduct", this.getProductCategory());
        model.addAttribute("listBrand", this.getProductBrand());
        model.addAttribute("listSize", criteriaRepository.findAllByStatus(Size.class));
        model.addAttribute("listColor", criteriaRepository.findAllByStatus(Color.class));
        model.addAttribute("listCategory", criteriaRepository.findAllByStatus(Category.class));
    }

    public Page<ObjectNode> getListProductDetail(int page, int size) {
        List<Product> productList = criteriaRepository.findAllByStatus(Product.class);

        List<ObjectNode> productNodes = productList.stream()
                .map(product -> JacksonEx.convertToType(product, ObjectNode.class))
                .collect(Collectors.toList());

        productNodes.forEach(nodeProduct -> {
            Long id = JacksonEx.getDataFromJsonNode(nodeProduct, "id", Long.class);
            if (id != null) {
                ArrayNode nodeDetail = JacksonEx.convertList2ArrayNode(criteriaRepository.getProductDetailsByIdParent(id));
                this.addNodeImage(nodeDetail);
                nodeProduct.put("listDetail", nodeDetail);
            }
        });

        Pageable pageable = PageRequest.of(page, size);
        return F4KUtils.toPage(productNodes, pageable);
    }

    private void addNodeImage(ArrayNode nodeList) {
        for (JsonNode node : nodeList) {
            ObjectNode objectNode = (ObjectNode) node;
            Long id = JacksonEx.getDataFromJsonNode(objectNode, "id", Long.class);
            if (id == null) continue;
            objectNode.put("listImage", JacksonEx.convertList2ArrayNode(criteriaRepository.getImgByProductDetailId(id)));
        }
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
