package org.example.quan_ao_f4k.controller.promotion;

import jakarta.validation.Valid;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionProductResponse;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.repository.promotion.PromotionProductRepository;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.example.quan_ao_f4k.service.pomotion.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/promotion")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private CriteriaRepository criteriaRepository;

    @Autowired
    private PromotionProductRepository productRepository;
    @Autowired
    private PromotionProductRepository promotionProductRepository;

    @GetMapping
    public String promotion(Model model) {
        List<Product> listProduct = criteriaRepository.findAllByStatus(Product.class);
        model.addAttribute("listProducts", criteriaRepository.findAllByStatus(Product.class));
        return "/admin/promotion/index";
    }

    @GetMapping("/get-list-promotion")
    @ResponseBody
    public Page<PromotionResponse> getListPromotion(@ModelAttribute PromotionRequest.RequestSearch request, Model model) {
        return promotionService.getListPromotion(request);
    }

    @GetMapping("/promotion-detail")
    @ResponseBody
    public ResponseEntity<?> detailPromotion(@RequestParam Long id) {
        return ResponseEntity.ok(promotionService.findDetail(id));
    }


    @PostMapping
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionRequest.Request request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        return promotionService.createPromotion(request);
    }

    @PutMapping
    public ResponseEntity<?> updatePromotion(@Valid @RequestBody PromotionRequest.Request request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        return promotionService.updatePromotion(request);
    }

}
