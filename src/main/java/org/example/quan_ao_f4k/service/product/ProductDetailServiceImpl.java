package org.example.quan_ao_f4k.service.product;

import jakarta.transaction.Transactional;
import org.example.quan_ao_f4k.dto.request.product.ProductDetailRequest;
import org.example.quan_ao_f4k.dto.response.product.ProductDetailResponse;
import org.example.quan_ao_f4k.dto.response.product.ProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.list.ListResponse;
import org.example.quan_ao_f4k.mapper.general.ImageMapper;
import org.example.quan_ao_f4k.mapper.product.ProductDetailMapper;
import org.example.quan_ao_f4k.model.general.Image;
import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.model.product.Product;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.model.product.Size;
import org.example.quan_ao_f4k.repository.general.ImageRepository;
import org.example.quan_ao_f4k.repository.order.CartProductRepository;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.product.ColorRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.repository.product.ProductRepository;
import org.example.quan_ao_f4k.repository.product.SizeRepository;
import org.example.quan_ao_f4k.service.common.IImageServiceImpl;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ProductDetailServiceImpl implements ProductDetailService {

    @Autowired
    private ProductDetailMapper productDetailMapper;
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private SizeRepository sizeRepository;
    @Autowired
    private ColorRepository colorRepository;
    @Autowired
    private CartProductRepository cartProductRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageMapper imageMapper;
    @Autowired
    private IImageServiceImpl iImageService;

    @Override
    public ListResponse<ProductDetailResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.PRODUCT_DETAIL, productDetailRepository, productDetailMapper);
    }

    @Override
    public ProductDetailResponse findById(Long aLong) {
        ProductDetail productDetail = productDetailRepository.findById(aLong).orElseThrow(
                () -> new BadRequestException("Không tìm thấy dữ liệu")
        );
        ProductDetailResponse productDetailResponse = productDetailMapper.entityToResponse(productDetail);
        List<Image> images = imageRepository.getImageByIdParent(productDetail.getId(), F4KConstants.TableCode.PRODUCT_DETAIL);
        productDetailResponse.setImages(imageMapper.entityToResponse(images));
        return productDetailResponse;
    }

    @Override
    public ProductDetailResponse save(ProductDetailRequest request) {
        return save(request);
    }

    @Override
    public ProductDetailResponse save(Long aLong, ProductDetailRequest request) {
        return null;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(List<Long> longs) {

    }


    @Override
    public ListResponse<ProductDetailResponse> getProductDetailByProductId(
            Long productId, int page, int size, String sort,
            String filter, String search, boolean all) {

        return defaultFindDetailsByProductId(
                productId, page, size, sort, filter, search, all,
                SearchFields.PRODUCT_DETAIL, productDetailRepository, productDetailMapper, "productDetail");
    }

    @Override
    @Transactional
    public ProductDetailResponse addProductDetail(Long productId, ProductDetailRequest request) {
        boolean exists = isAddExistsByProductSizeAndColor(productId, request.getSizeId(), request.getColorId());
        if (exists) {
            throw new BadRequestException("Sản phẩm có màu và kích cỡ này đã tồn tại");
        }
        productRepository.findById(productId);
        request.setProductId(productId);
        ProductDetailResponse response = defaultSave(request, productDetailRepository, productDetailMapper);
        if (request.getImages() != null) {
            request.getImages().forEach(el -> saveOrUpdateImage(el, response));
        }
        return response;
    }

    @Override
    @Transactional
    public ProductDetailResponse updateProductDetail(Long productId, Long id, ProductDetailRequest request) {
        boolean exists = isUpdateExistsByProductSizeAndColor(productId,
                request.getSizeId(), request.getColorId(), id);
        if (exists) throw new BadRequestException("Sản phẩm có màu và kích cỡ này đã tồn tại");

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        ProductDetail productDetail = productDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chi tiết sản phẩm không tồn tại"));
        Size size = sizeRepository.findById(request.getSizeId())
                .orElseThrow(() -> new RuntimeException("Kích thước không tồn tại"));
        Color color = colorRepository.findById(request.getColorId())
                .orElseThrow(() -> new RuntimeException("Màu sắc không tồn tại"));
        productDetail.setProduct(product);
        productDetail.setSize(size);
        productDetail.setColor(color);
        productDetail.setPrice(request.getPrice());
        productDetail.setQuantity(request.getQuantity());
        productDetail.setStatus(request.getStatus());

        ProductDetail obj = productDetailRepository.save(productDetail);

        if (request.getOldFiles() != null) {
            imageRepository.deleteImagesByParentIdAndTableCodeNotIn(productDetail.getId(), F4KConstants.TableCode.PRODUCT_DETAIL, request.getOldFiles());
        }
        if (request.getImages() != null) {
            request.getImages().forEach(el -> saveOrUpdateImage(el, productDetailMapper.entityToResponse(obj)));
        }

        return productDetailMapper.entityToResponse(obj);
    }

    @Override
    public boolean isAddExistsByProductSizeAndColor(Long productId, Long sizeId, Long colorId) {
        return productDetailRepository.isAddExistsByProductSizeAndColor(productId, sizeId, colorId);
    }

    @Override
    public boolean isUpdateExistsByProductSizeAndColor(Long productId, Long sizeId, Long colorId, Long id) {
        return productDetailRepository.isUpdateExistsByProductSizeAndColorId(productId, sizeId, colorId, id);
    }

    @Override
    public boolean deleteProductDetail(Long productId, Long id) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isPresent()) {
            List<ProductDetail> listProductDetail = productDetailRepository.findProductDetailsByProductId(product.get().getId());
            for (ProductDetail productDetail : listProductDetail) {
                if(productDetail.getId().equals(id)){
                    boolean hasConstraints = checkConstraints(id);
                    if (hasConstraints) {

                        return false;
                    }
                    productDetailRepository.deleteById(id);
                    return true;
                }
            }
        }
        return false;
    }


    private void saveOrUpdateImage(MultipartFile file, ProductDetailResponse productDetail) {
        try {
            String fileName = iImageService.save(file, productDetail.getProduct().getSlug());
            Image objImage = Image.builder()
                    .idParent(productDetail.getId())
                    .nameFile(file.getOriginalFilename())
                    .size(file.getSize())
                    .status(F4KConstants.STATUS_ON)
                    .tableCode(F4KConstants.TableCode.PRODUCT_DETAIL)
                    .path(fileName)
                    .fileUrl(iImageService.getPublicImageUrl(fileName))
                    .build();

            imageRepository.save(objImage);
        } catch (IOException e) {
            throw new BadRequestException("Gặp lỗi khi upload file!");
        }
    }

    private boolean checkConstraints(Long isParent) {
        int orderCount = orderDetailRepository.countByProductDetail(isParent);
        int cartCount = cartProductRepository.countByProductDetail(isParent);

        return (orderCount > 0 || cartCount > 0);
    }

}
