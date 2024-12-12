package org.example.quan_ao_f4k.service.pomotion;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionProductRequest;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.product.ProductMapper;
import org.example.quan_ao_f4k.mapper.promotion.PromotionMapper;
import org.example.quan_ao_f4k.mapper.promotion.PromotionProductMapper;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.model.promotion.PromotionProduct;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.repository.promotion.PromotionProductRepository;
import org.example.quan_ao_f4k.repository.promotion.PromotionRepository;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;

    private final PromotionMapper promotionMapper;
    private final PromotionProductMapper promotionProductMapper;
    private final ProductMapper productMapper;

    @Override
    public ListResponse<PromotionResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        updateExpiredPromotionsBatch();
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.PROMOTION, promotionRepository, promotionMapper);
    }

    @Override
    public PromotionResponse findById(Long aLong) {
        Promotion object = promotionRepository.findById(aLong).orElseThrow(
                () -> new BadRequestException("Lỗi: không tìm thấy bản ghi")
        );
        PromotionResponse response = promotionMapper.entityToResponse(object);
        response.setProducts(
                promotionProductMapper.entityToResponse(promotionProductRepository.findByPromotionId(response.getId(), F4KConstants.STATUS_ON))
        );
        return response;
    }

    private void quickCheckPromotion(PromotionRequest request) {
        if (!Objects.equals(request.getStatus() + "", F4KConstants.STATUS_ON + "") && !Objects.equals(request.getStatus() + "", F4KConstants.STATUS_OFF + "")) {
            throw new BadRequestException("Trang thái không phù hợp vui lòng kiểm tra lại");
        }
    }

    @Override
    @Transactional
    public PromotionResponse save(PromotionRequest request) {
        Promotion exitsPromotion = promotionRepository.findByNameAndDate(request.getName(), request.getDayStart(), null).orElse(null);
        if (exitsPromotion != null) {
            throw new BadRequestException(String.format("Đã tồn tại sự kiện có tên [%s] từ ngày [%s] đến ngày [%s] hãy cập nhật lại hoặc tạo mới", request.getName(), exitsPromotion.getDayStart(), exitsPromotion.getDayEnd()));
        }

        Promotion promotionTmp = promotionMapper.requestToEntity(request);
        promotionTmp.setStatus(F4KConstants.STATUS_ON);
        promotionTmp.setDiscountValue(request.getDiscountValue());
        Promotion promotion = promotionRepository.save(promotionTmp);
        for (Long idProduct : request.getProductIds()) {
            PromotionProduct promotionProduct = PromotionProduct
                    .builder()
                    .promotion(promotion)
                    .status(F4KConstants.STATUS_ON)
                    .type(F4KConstants.TYPE_PERCENT)
                    .productDetail(productDetailRepository.findById(idProduct).get())
                    .build();
            promotionProductRepository.save(promotionProduct);
        }

        return promotionMapper.entityToResponse(promotion);
    }

    @Override
    @Transactional
    public PromotionResponse save(Long aLong, PromotionRequest request) {
        quickCheckPromotion(request);
        Promotion object = promotionRepository.findById(aLong).orElseThrow(
                () -> new BadRequestException("Lỗi: không tìm thấy bản ghi ")
        );

        Promotion exitsPromotion = promotionRepository.findByNameAndDate(request.getName(), request.getDayStart(), request.getId()).orElse(null);
        if (exitsPromotion != null) {
            throw new BadRequestException(String.format("Đã tồn tại sự kiện có tên [%s] từ ngày [%s] đến ngày [%s] hãy cập nhật lại hoặc tạo mới", request.getName(), exitsPromotion.getDayStart(), exitsPromotion.getDayEnd()));
        }

        object.setName(request.getName());
        object.setUpdatedAt(LocalDateTime.now());
        object.setStatus(request.getStatus());
        object.setDayStart(request.getDayStart());
        object.setDayEnd(request.getDayEnd());
        object.setDiscountValue(request.getDiscountValue());

        Promotion promotionSave = promotionRepository.save(object);
        promotionProductRepository.deleteAllByPromotion_Id(object.getId());
        for (Long idProduct : request.getProductIds()) {
            PromotionProduct promotionProduct = PromotionProduct
                    .builder()
                    .promotion(promotionSave)
                    .status(F4KConstants.STATUS_ON)
                    .type(F4KConstants.TYPE_PERCENT)
                    .productDetail(productDetailRepository.findById(idProduct).get())
                    .build();
            promotionProductRepository.save(promotionProduct);
        }
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(List<Long> longs) {

    }

    @Override
    public Page<PromotionResponse> searchPromotion(int page, int size, String search, Integer status, Integer effectiveDate) {
        updateExpiredPromotionsBatch();
        List<Promotion> listPromotion = promotionRepository.findPromotionsByRequest(search, status, effectiveDate);
        Pageable pageable = PageRequest.of(page - 1, size);
        return F4KUtils.toPage(promotionMapper.entityToResponse(listPromotion), pageable);
    }

    @Override
    public void addModelPromotionAdd(Model model) {
        List<Product> productList = productRepository.findProductByStatus(F4KConstants.STATUS_ON);
        model.addAttribute("listProducts", productMapper.entityToResponse(productList));
    }

    @Override
    public List<Promotion> getActivePromotions() {
        LocalDate now = LocalDate.now(); // Lấy ngày hiện tại
        return promotionRepository.findAllByStatusAndDayStartBeforeAndDayEndAfter(F4KConstants.STATUS_ON, now);
    }

    @Override
    public Promotion getBestPromotionForProductDetail(Long productDetailId) {
        LocalDate now = LocalDate.now();
        List<Promotion> promotions = promotionRepository.findActivePromotionsByProductDetailId(productDetailId, now);
        return promotions.isEmpty() ? null : promotions.get(0);
    }

    @Override
    public BigDecimal calculateDiscountedPrice(BigDecimal originalPrice, BigDecimal discountPercent) {
        if (originalPrice == null || discountPercent == null) {
            return originalPrice;
        }

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải nằm trong khoảng từ 0 đến 100.");
        }

        BigDecimal discountMultiplier = BigDecimal.ONE.subtract(
                discountPercent.divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP)
        );

        return originalPrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private void updateExpiredPromotionsBatch() {
        List<Promotion> listPromotion = promotionRepository.findAll();
        List<Promotion> updatedPromotions = new ArrayList<>();

        for (Promotion promotion : listPromotion) {
            if (promotion.getStatus() == 0) {
                continue; // Bỏ qua chương trình khuyến mãi khi trangj thái vô hiệu
            }

            LocalDate now = LocalDate.now();
            if (promotion.getDayEnd().isBefore(now)) {
                promotion.setStatus(2); // Cập nhật trạng thái thành 2 (hết hạn)
                updatedPromotions.add(promotion);
            } else if (promotion.getDayStart().isAfter(now)) {
                promotion.setStatus(3); // Cập nhật trạng thái thành 3 (sắp diễn ra)
                updatedPromotions.add(promotion);
            }
        }

        if (!updatedPromotions.isEmpty()) {
            promotionRepository.saveAll(updatedPromotions); // Lưu batch
        }
    }
}
