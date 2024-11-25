package org.example.quan_ao_f4k.service.shop;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.response.shop.ShopProductResponse;
import org.example.quan_ao_f4k.exception.BadRequestException;
import org.example.quan_ao_f4k.mapper.shop.ShopProductMapper;
import org.example.quan_ao_f4k.model.order.Cart;
import org.example.quan_ao_f4k.model.order.CartProduct;
import org.example.quan_ao_f4k.model.order.ShippingInfo;
import org.example.quan_ao_f4k.model.product.ProductDetail;
import org.example.quan_ao_f4k.repository.order.CartProductRepository;
import org.example.quan_ao_f4k.repository.order.CartRepository;
import org.example.quan_ao_f4k.repository.order.OrderDetailRepository;
import org.example.quan_ao_f4k.repository.order.ShippingInfoRepository;
import org.example.quan_ao_f4k.repository.product.ProductDetailRepository;
import org.example.quan_ao_f4k.util.DeliveryForShopUtils;
import org.example.quan_ao_f4k.util.F4KConstants;
import org.example.quan_ao_f4k.util.F4KUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class ShopCartServiceImpl implements ShopCartService{
    private final F4KUtils f4KUtils;

    private final CartRepository cartRepository;
    private final CartProductRepository cartProductRepository;
    private final ShippingInfoRepository shippingInfoRepository;
    private final ProductDetailRepository productDetailRepository;

    private final ShopProductMapper shopProductMapper;

    @Override
    public void addModelCart(Model model) {
        ShopProductResponse.CartResponse cartResponse = getListCart(f4KUtils.getUser().getUsername());
        model.addAttribute("carts", cartResponse);
    }

    @Override
    public ShopProductResponse.CartResponse getListCart(String username) {
        Cart cart = getCart(f4KUtils.getUser().getId());
        List<CartProduct> listCartProduct = cartProductRepository.findAllByCart_Id(cart.getId());
        for (CartProduct cartProduct : listCartProduct) {
            if (cartProduct.getProductDetail().getStatus() == F4KConstants.STATUS_OFF){
                cartProduct.setStatus(F4KConstants.HET_HANG);
            } else {
                int quantity = productDetailRepository.findQuantityByProductDetailId(cartProduct.getProductDetail().getId());
                cartProduct.setStatus(F4KConstants.CON_HANG);
                if (quantity <= 0) {
                    cartProduct.setStatus(F4KConstants.HET_HANG);
                } else if (cartProduct.getQuantity() > quantity){
                    cartProduct.setQuantity(quantity);
                }
            }
            cartProductRepository.save(cartProduct);
        }

        List<ShopProductResponse.CartProductDto> listCartProductDto = shopProductMapper.toCartProductDto(listCartProduct);
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ShopProductResponse.CartProductDto el : listCartProductDto) {
            el.setTotal(el.getProductDetailDto().getPrice().multiply(BigDecimal.valueOf(el.getQuantity())));
            if (el.getStatus() == F4KConstants.CON_HANG) {
                subtotal = subtotal.add(el.getProductDetailDto().getPrice().multiply(BigDecimal.valueOf(el.getQuantity())));
            }
        }

        return ShopProductResponse.CartResponse.builder()
                .items(listCartProductDto)
                .itemCount(listCartProductDto.size())
                .subtotal(subtotal)
                .build();
    }

    @Override
    @Transactional
    public String addCart(Long idProductDetail, int quantity) {
        try {
            ProductDetail productDetail = productDetailRepository.findById(idProductDetail).orElseThrow(
                    () -> new BadRequestException("Không tồn tại sản phẩm!")
            );

            if (productDetail.getQuantity() < quantity) {
                throw new BadRequestException("Số lượng sản phẩm không đủ");
            }
            Cart cart = getCart(f4KUtils.getUser().getId());
            CartProduct cartProductTmp = cartProductRepository.findByCart_IdAndProductDetail_Id(cart.getId(), productDetail.getId()).orElse(null);
            CartProduct cartProduct = new CartProduct();

            if (cartProductTmp == null) {
                cartProduct.setCart(cart);
                cartProduct.setQuantity(quantity);
                cartProduct.setProductDetail(productDetail);
                cartProduct.setCreatedAt(LocalDateTime.now());
                cartProduct.setStatus(F4KConstants.CON_HANG);
            } else {
                int productQuantity = cartProductTmp.getQuantity() + quantity;
                if (productQuantity > productDetail.getQuantity()) {
                    productQuantity = productDetail.getQuantity();
                }
                cartProduct = cartProductTmp;
                cartProduct.setQuantity(productQuantity);
            }
            cartProductRepository.save(cartProduct);
            return cartProductRepository.countProductDetailByCartId(cart.getId()) + "";
        } catch (RuntimeException e) {
            throw new BadRequestException("Đã có lỗi xảy ra xin hãy thao tác lại");
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long idProductDetail, int quantity) {
        ProductDetail productDetail = productDetailRepository.findById(idProductDetail).orElseThrow(
                () -> new BadRequestException("Không tồn tại sản phẩm!")
        );

        if (quantity < 0) {
            throw new BadRequestException("Số lượng không hợp lệ!");
        }

        Cart cart = getCart(f4KUtils.getUser().getId());
        CartProduct cartProductTmp = cartProductRepository.findByCart_IdAndProductDetail_Id(cart.getId(), productDetail.getId()).orElse(null);

        if (cartProductTmp == null) {
            throw new BadRequestException("Không tồn tại sản phẩm trong giỏ hàng");
        }

        if (productDetail.getQuantity() < quantity) {
            throw new BadRequestException("Số lượng sản phẩm không đủ");
        }

        cartProductTmp.setQuantity(quantity);

        cartProductRepository.save(cartProductTmp);
    }


    @Override
    @Transactional
    public void removeCart(Long idProductDetail) {
        try {
            ProductDetail productDetail = productDetailRepository.findById(idProductDetail).orElseThrow(
                    () -> new BadRequestException("Không tồn tại sản phẩm!")
            );
            Cart cart = getCart(f4KUtils.getUser().getId());
            cartProductRepository.findByCart_IdAndProductDetail_Id(cart.getId(), productDetail.getId()).ifPresent(cartProductRepository::delete);
        } catch (RuntimeException e) {
            throw new BadRequestException("Đã có lỗi xảy ra xin hãy thao tác lại");
        }
    }

    @Override
    public Cart getCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            cart = cartRepository.save(Cart.builder()
                    .status(F4KConstants.STATUS_ON)
                    .user(f4KUtils.getUser())
                    .build());
        }
        return cart;
    }


    @Override
    public ShopProductResponse.ShippingInfoDto detailShippingInfo(Long idProductDetail) {
        ShippingInfo shippingInfo = shippingInfoRepository.findById(idProductDetail).orElseThrow(
                () -> new BadRequestException("Không tồn tại địa chỉ, vui lòng xem lại!")
        );
        return shopProductMapper.toShippingInfoDto(shippingInfo);
    }

    @Override
    public String getFree(int districtId, String wardCode) {
        return DeliveryForShopUtils.getFree(districtId, wardCode);
    }

    @Override
    public String getProvince() {
        ResponseEntity<String> response = DeliveryForShopUtils.getProvince();
        if (response == null) {
            throw new BadRequestException("Đã có lỗi trong quá trình lấy thông tin xin vui lòng load lại trang và thử lại!");
        }
        return response.getBody();
    }

    @Override
    public String getDistrict(int provinceId) {
        ResponseEntity<String> response = DeliveryForShopUtils.getDistrict(provinceId);
        if (response == null) {
            throw new BadRequestException("Đã có lỗi trong quá trình lấy thông tin xin vui lòng load lại trang và thử lại!");
        }
        return response.getBody();
    }

    @Override
    public String getWard(int districtId) {
        ResponseEntity<String> response = DeliveryForShopUtils.getWard(districtId);
        if (response == null) {
            throw new BadRequestException("Đã có lỗi trong quá trình lấy thông tin xin vui lòng load lại trang và thử lại!");
        }
        return response.getBody();
    }

    @Override
    @Transactional
    public void addShippingInfo(ShopProductResponse.ShippingInfoDto shippingInfoDto) {
        ShippingInfo shippingInfo = shopProductMapper.toShippingInfo(shippingInfoDto);
        if (shippingInfo.getIsDefault()) {
            ShippingInfo objTmp = shippingInfoRepository.findDefaultByUserId(f4KUtils.getUser().getId());
            objTmp.setIsDefault(false);
            shippingInfoRepository.save(objTmp);
        }
        shippingInfo.setUser(f4KUtils.getUser());
        shippingInfoRepository.save(shippingInfo);
    }

    @Override
    @Transactional
    public void setDefaultAddress(Long shippingId) {
        ShippingInfo shippingInfo = shippingInfoRepository.findById(shippingId).orElseThrow(
                () -> new BadRequestException("Địa chỉ hàng không phù hợp, vui lòng xem lại")
        );
        ShippingInfo objTmp = shippingInfoRepository.findDefaultByUserId(f4KUtils.getUser().getId());
        objTmp.setIsDefault(false);
        shippingInfoRepository.save(objTmp);

        shippingInfo.setIsDefault(true);
        shippingInfoRepository.save(shippingInfo);
    }

    @Override
    public List<ShopProductResponse.ShippingInfoDto> getShippingInfo() {
        List<ShippingInfo> list = shippingInfoRepository.findAllByUserId(f4KUtils.getUser().getId());
        return shopProductMapper.toShippingInfoDto(list);
    }
}
