package org.example.quan_ao_f4k.controller.promotion;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.model.product.Product;
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

@Controller
@RequestMapping("${api.prefix}/admin/promotion")
@AllArgsConstructor
public class PromotionController {

    @Autowired
    private PromotionService promotionService;
	@Autowired
	private CriteriaRepository criteriaRepository;


    @GetMapping({"promotion/","promotion/"})
    public String promotion(Model model) {
        model.addAttribute("listProducts", criteriaRepository.findAllByStatus(Product.class));
        return "/admin/promotion/promotion";
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
}
