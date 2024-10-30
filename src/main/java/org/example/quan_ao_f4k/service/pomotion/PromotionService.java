package org.example.quan_ao_f4k.service.pomotion;

import jakarta.transaction.Transactional;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionProductResponse;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.model.promotion.PromotionProduct;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.repository.promotion.PromotionProductRepository;
import org.example.quan_ao_f4k.repository.promotion.PromotionRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionProductRepository promotionProductRepository;

    @Autowired
    private ProductRepository productRepository;

    public Page<PromotionResponse> getListPromotion(PromotionRequest.RequestSearch request) {
        List<Promotion> listPromotion = promotionRepository.findPromotionsByRequest(request.getName(), request.getStatus(), request.getEffectiveDate());

        List<PromotionResponse> listResponse = listPromotion.stream().map(el -> PromotionResponse.builder()
                .id(el.getId())
                .name(el.getName())
                .status(el.getStatus())
                .dayStart(el.getDayStart())
                .dayEnd(el.getDayEnd())
                .products(promotionProductRepository.findByPromotionId(el.getId(), null))
                .build()).toList();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return F4KUtils.toPage(listResponse, pageable);
    }

    public PromotionResponse findDetail(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Không tồn tại chương trình khuyến mãi trong dữ liệu!"));

        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .status(promotion.getStatus())
                .dayStart(promotion.getDayStart())
                .dayEnd(promotion.getDayEnd())
                .products(promotionProductRepository.findByPromotionId(promotion.getId(), null))
                .build();
    }

    @Transactional
    public ResponseEntity<?> createPromotion(PromotionRequest.Request request) {
        if (promotionRepository.existsByNameAndDate(request.getName(), request.getDayStart(), null)) {
            throw new BadRequestException("Đã tồn tại một sự kiện tương tự từ ngày " + request.getDayStart() + " đến " + request.getDayEnd());
        }
        if (!Objects.equals(request.getStatus(), F4KConstants.STATUS_ON) && !Objects.equals(request.getStatus(), F4KConstants.STATUS_OFF)) {
            throw new BadRequestException("Trang thái không phù hợp vui lòng kiểm tra lại");
        }

        Promotion objTmp = promotionRepository.save(
                Promotion.builder()
                        .name(request.getName())
                        .status(request.getStatus())
                        .dayStart(request.getDayStart())
                        .dayEnd(request.getDayEnd())
                        .build()
        );

        for (PromotionRequest.ProductDto item : removeDuplicates(request.getProducts())) {
            Product product = productRepository.findById(item.getProduct()).orElseThrow(
                    () -> new BadRequestException("Không tồn tại sản phẩm, Vui lòng kiểm tra lại!")
            );

            if (!Objects.equals(item.getDiscountType(), F4KConstants.TYPE_CASH) && !Objects.equals(item.getDiscountType(), F4KConstants.TYPE_PERCENT)) {
                throw new BadRequestException("Loại giá trị giảm không phù hợp vui lòng kiểm tra lại");
            }

            if (Objects.equals(item.getDiscountType(), F4KConstants.TYPE_PERCENT) && item.getDiscountType() > 100) {
                throw new BadRequestException("Giá trị giảm không phù hợp vui lòng kiểm tra lại");
            }

            PromotionProduct promotionProduct = PromotionProduct.builder()
                    .product(product)
                    .discountValue(item.getDiscount())
                    .type(item.getDiscountType())
                    .promotion(objTmp)
                    .build();

            promotionProductRepository.save(promotionProduct);
        }

        return ResponseEntity.status(HttpStatus.OK).body(objTmp);
    }

    private List<PromotionRequest.ProductDto> removeDuplicates(List<PromotionRequest.ProductDto> items) {
        Set<Long> productDtoSet = new HashSet<>();
        List<PromotionRequest.ProductDto> listItem = new ArrayList<>();

        for (PromotionRequest.ProductDto item: items) {
            if (!productDtoSet.contains(item.getProduct())) {
                productDtoSet.add(item.getProduct());
                listItem.add(item);
            }
        }

        return listItem;
    }

    @Transactional
    public ResponseEntity<?> updatePromotion(PromotionRequest.Request request) {
        if (promotionRepository.existsByNameAndDate(request.getName(), request.getDayStart(), request.getId())) {
            throw new BadRequestException("Đã tồn tại một sự kiện tương tự từ ngày " + request.getDayStart() + " đến " + request.getDayEnd());
        }

        Promotion objTmp = new Promotion();
        objTmp.setId(request.getId());
        objTmp.setName(request.getName());
        objTmp.setStatus(request.getStatus());
        objTmp.setDayStart(request.getDayStart());
        objTmp.setDayEnd(request.getDayEnd());
        promotionRepository.save(objTmp);

        promotionProductRepository.deleteAllByPromotion_Id(objTmp.getId());
        for (PromotionRequest.ProductDto item : removeDuplicates(request.getProducts())) {
            Product product = productRepository.findById(item.getProduct()).orElseThrow(
                    () -> new BadRequestException("Không tồn tại sản phẩm, Vui lòng kiểm tra lại!")
            );

            if (!Objects.equals(item.getDiscountType(), F4KConstants.TYPE_CASH) && !Objects.equals(item.getDiscountType(), F4KConstants.TYPE_PERCENT)) {
                throw new BadRequestException("Loại giá trị giảm không phù hợp vui lòng kiểm tra lại");
            }

            if (Objects.equals(item.getDiscountType(), F4KConstants.TYPE_PERCENT) && item.getDiscountType() > 100) {
                throw new BadRequestException("Giá trị giảm không phù hợp vui lòng kiểm tra lại");
            }

            PromotionProduct promotionProduct = PromotionProduct.builder()
                    .product(product)
                    .discountValue(item.getDiscount())
                    .type(item.getDiscountType())
                    .promotion(objTmp)
                    .build();

            promotionProductRepository.save(promotionProduct);
        }
        return ResponseEntity.status(HttpStatus.OK).body(promotionRepository.save(objTmp));
    }
}
