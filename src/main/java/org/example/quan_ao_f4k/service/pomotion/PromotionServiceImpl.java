package org.example.quan_ao_f4k.service.pomotion;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionProductRequest;
import org.example.quan_ao_f4k.dto.request.promotion.PromotionRequest;
import org.example.quan_ao_f4k.dto.response.promotion.PromotionResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.promotion.PromotionMapper;
import org.example.quan_ao_f4k.mapper.promotion.PromotionProductMapper;
import org.example.quan_ao_f4k.model.promotion.Promotion;
import org.example.quan_ao_f4k.model.promotion.PromotionProduct;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionMapper promotionMapper;
    private final PromotionProductMapper promotionProductMapper;
    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final ProductRepository productRepository;

    @Override
    public ListResponse<PromotionResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.PROMOTION, promotionRepository, promotionMapper);
    }

    @Override
    public PromotionResponse findById(Long aLong) {
        Promotion object = promotionRepository.findById(aLong).orElseThrow(
                () -> new BadRequestException("Lỗi: không tìm thấy bản ghi ")
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

    private void quickCheckPromotionProduct(PromotionProductRequest request) {

//        ProductDetail productDetail =
        if (!productRepository.existsById(request.getProductId())) {
            throw new BadRequestException("Không tồn tại sản phẩm, Vui lòng kiểm tra lại!");
        }

        if (!Objects.equals(request.getType() + "", F4KConstants.TYPE_CASH + "") && !Objects.equals(request.getType()  + "", F4KConstants.TYPE_PERCENT + "")) {
            throw new BadRequestException("Loại giá trị giảm không phù hợp vui lòng kiểm tra lại");
        }

        if (Objects.equals(request.getType() + "", F4KConstants.TYPE_PERCENT +"") && request.getDiscountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new BadRequestException("Giá trị giảm không phù hợp vui lòng kiểm tra lại");
        }
    }

    @Override
    @Transactional
    public PromotionResponse save(PromotionRequest request) {
        quickCheckPromotion(request);

        if (promotionRepository.findByStarDate(request.getDayStart(), null).isPresent()) {
            throw new BadRequestException("Không thể cập nhật vì đã tồn tại một sự kiện tương tự từ ngày " + request.getDayStart() + " đến " + request.getDayEnd());
        }

        Promotion promotion = promotionRepository.save(promotionMapper.requestToEntity(request));
        for (PromotionProductRequest item : removeDuplicates(request.getProducts())) {
            quickCheckPromotionProduct(item);
            item.setPromotionId(promotion.getId());
            item.setStatus(F4KConstants.STATUS_ON);
            PromotionProduct promotionProduct = promotionProductMapper.requestToEntity(item);
            promotionProduct.setStatus(F4KConstants.STATUS_ON);
            promotionProductRepository.save(promotionProduct);
        }

        return promotionMapper.entityToResponse(promotion);
    }

    @Override
    @Transactional
    public PromotionResponse save(Long aLong, PromotionRequest request) {
        Promotion object = promotionRepository.findById(aLong).orElseThrow(
                () -> new BadRequestException("Lỗi: không tìm thấy bản ghi ")
        );

        quickCheckPromotion(request);
        if (promotionRepository.findByStarDate(request.getDayStart(), aLong).isPresent()) {
            throw new BadRequestException("Không thể cập nhật vì đã tồn tại một sự kiện tương tự từ ngày " + request.getDayStart() + " đến " + request.getDayEnd());
        }

        object.setName(request.getName());
        object.setUpdatedAt(LocalDateTime.now());
        object.setStatus(request.getStatus());
        object.setDayStart(request.getDayStart());
        object.setDayEnd(request.getDayEnd());
        promotionRepository.save(object);

        promotionProductRepository.deleteAllByPromotion_Id(object.getId());
        for (PromotionProductRequest item : removeDuplicates(request.getProducts())) {
            quickCheckPromotionProduct(item);

            item.setPromotionId(object.getId());
            PromotionProduct promotionProduct = promotionProductMapper.requestToEntity(item);

            promotionProduct.setStatus(F4KConstants.STATUS_ON);
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
        List<Promotion> listPromotion = promotionRepository.findPromotionsByRequest(search, status, effectiveDate);
        Pageable pageable = PageRequest.of(page - 1, size);
        return F4KUtils.toPage(promotionMapper.entityToResponse(listPromotion), pageable);
    }

    private List<PromotionProductRequest> removeDuplicates(List<PromotionProductRequest> items) {
        Set<Long> productDtoSet = new HashSet<>();
        List<PromotionProductRequest> listItem = new ArrayList<>();
        for (PromotionProductRequest item : items) {
            if (!productDtoSet.contains(item.getProductId())) {
                productDtoSet.add(item.getProductId());
                listItem.add(item);
            }
        }
        return listItem;
    }
}
