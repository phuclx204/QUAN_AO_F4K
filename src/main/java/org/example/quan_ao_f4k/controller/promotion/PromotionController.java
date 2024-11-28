package org.example.quan_ao_f4k.controller.promotion;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.repository.promotion.PromotionRepository;
import org.example.quan_ao_f4k.repository.shop.CriteriaRepository;
import org.example.quan_ao_f4k.service.pomotion.PromotionService;
import org.example.quan_ao_f4k.util.F4KConstants;
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
@AllArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    private final CriteriaRepository criteriaRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    @GetMapping
    public String promotion(Model model) {
        model.addAttribute("listProducts", criteriaRepository.findAllByStatus(Product.class));
        return "/admin/promotion/promotion";
    }

    @GetMapping("/add")
    public String addPromotion(Model model) {
        promotionService.addModelPromotionAdd(model);
        return "/admin/promotion/promotion_add";
    }

    @GetMapping("/update/{id}")
    public String updatePromotion(@PathVariable Long id, Model model) {
        Promotion promotion = promotionRepository.findById(id).orElse(null);
        if (promotion == null) {
            return "error/error_404";
        } else {
            model.addAttribute("promotion", promotion);
            return "/admin/promotion/promotion_update";
        }
    }

    @GetMapping("/list")
    @ResponseBody
    public ResponseEntity<Page<PromotionResponse>> getListPromotion(@RequestParam(defaultValue = "1") int page,
                                                                            @RequestParam(defaultValue = "5") int size,
                                                                            @RequestParam(required = false) String search,
                                                                            @RequestParam(required = false) Integer status,
                                                                            @RequestParam(required = false) Integer effectiveDate
    ) {
        return ResponseEntity.ok(promotionService.searchPromotion(page, size, search, status, effectiveDate));
    }

    @GetMapping("/detail")
    @ResponseBody
    public ResponseEntity<?> detailPromotion(@RequestParam Long id) {
        return ResponseEntity.ok(promotionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> createPromotion(@Valid @RequestBody PromotionRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        return ResponseEntity.ok(promotionService.save(request));
    }

    @PutMapping
    public ResponseEntity<?> updatePromotion(@Valid @RequestBody PromotionRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }
        return ResponseEntity.ok(promotionService.save(request.getId(), request));
    }

    @PutMapping("/update-status/{id}")
    public ResponseEntity<?> updateStatusPromotion(@PathVariable Long id, @RequestParam Integer status) {
        Promotion promotion = promotionRepository.findById(id).orElse(null);
        if (promotion == null) {
            throw new BadRequestException("Không tìm thấy bản ghi");
        }
        promotion.setStatus(status);
        promotionRepository.save(promotion);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-promotions")
    public ResponseEntity<List<Promotion>> getListPromotion() {
        return ResponseEntity.ok(promotionService.getActivePromotions());
    }
}
