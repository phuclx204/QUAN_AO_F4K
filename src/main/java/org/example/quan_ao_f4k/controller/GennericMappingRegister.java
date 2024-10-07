package org.example.quan_ao_f4k.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.address.AddressRequest;
import org.example.quan_ao_f4k.dto.request.address.DistrictRequest;
import org.example.quan_ao_f4k.dto.request.address.ProvinceRequest;
import org.example.quan_ao_f4k.dto.request.address.WardRequest;
import org.example.quan_ao_f4k.dto.request.product.*;
import org.example.quan_ao_f4k.dto.response.address.AddressResponse;
import org.example.quan_ao_f4k.dto.response.address.DistrictResponse;
import org.example.quan_ao_f4k.dto.response.address.ProvinceResponse;
import org.example.quan_ao_f4k.dto.response.address.WardResponse;
import org.example.quan_ao_f4k.dto.response.product.*;
import org.example.quan_ao_f4k.mapper.address.AddressMapper;
import org.example.quan_ao_f4k.mapper.address.DistrictMapper;
import org.example.quan_ao_f4k.mapper.address.ProvinceMapper;
import org.example.quan_ao_f4k.mapper.address.WardMapper;
import org.example.quan_ao_f4k.mapper.product.*;
import org.example.quan_ao_f4k.model.address.Address;
import org.example.quan_ao_f4k.model.address.District;
import org.example.quan_ao_f4k.model.address.Province;
import org.example.quan_ao_f4k.model.address.Ward;
import org.example.quan_ao_f4k.model.product.*;
import org.example.quan_ao_f4k.repository.address.AddressRepository;
import org.example.quan_ao_f4k.repository.address.DistrictRepository;
import org.example.quan_ao_f4k.repository.address.ProvinceRepository;
import org.example.quan_ao_f4k.repository.address.WardRepository;
import org.example.quan_ao_f4k.repository.product.*;
import org.example.quan_ao_f4k.service.CrudService;
import org.example.quan_ao_f4k.service.GenericService;
import org.example.quan_ao_f4k.util.SearchFields;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

@Component
@AllArgsConstructor
public class GennericMappingRegister {
    private ApplicationContext context;
    private RequestMappingHandlerMapping handlerMapping;

    private GenericController<BrandRequest, BrandResponse> brandController;
    private GenericController<CategoryRequest, CategoryResponse> categoryController;
    private GenericController<SizeRequest, SizeResponse> sizeController;
    private GenericController<ColorRequest, ColorResponse> colorController;
    private GenericController<ProductRequest, ProductResponse> productController;
    private GenericController<ProductDetailRequest, ProductDetailResponse> productDetailController;

    private GenericController<ProvinceRequest, ProvinceResponse> provinceController;
    private GenericController<DistrictRequest, DistrictResponse> districtController;
    private GenericController<WardRequest, WardResponse> wardController;
    private GenericController<AddressRequest, AddressResponse> addressController;
    private GenericController<GuaranteeRequest, GuaranteeResponse> guaranteeController;


    private GenericService<Brand, BrandRequest, BrandResponse> brandService;
    private GenericService<Category, CategoryRequest, CategoryResponse> categoryService;
    private GenericService<Size, SizeRequest, SizeResponse> sizeService;
    private GenericService<Color, ColorRequest, ColorResponse> colorService;
    private GenericService<Product, ProductRequest, ProductResponse> productService;
    private GenericService<ProductDetail, ProductDetailRequest, ProductDetailResponse> productDetailService;
    private GenericService<Guarantee, GuaranteeRequest, GuaranteeResponse> guaranteeService;

    private GenericService<Province, ProvinceRequest, ProvinceResponse> provinceService;
    private GenericService<District, DistrictRequest, DistrictResponse> districtService;
    private GenericService<Ward, WardRequest, WardResponse> wardService;
    private GenericService<Address, AddressRequest, AddressResponse> addressService;


    @PostConstruct
    public void registerControllers() throws NoSuchMethodException {

        // product

        register("product", productController, productService.init(
                context.getBean(ProductRepository.class),
                context.getBean(ProductMapper.class),
                SearchFields.PRODUCT,"products"

        ), ProductRequest.class);

        register("brand", brandController, brandService.init(
                context.getBean(BrandRepository.class),
                context.getBean(BrandMapper.class),
                SearchFields.BRAND,"brands"

        ), BrandRequest.class);

        register("category", categoryController, categoryService.init(
                context.getBean(CategoryRepository.class),
                context.getBean(CategoryMapper.class),
                SearchFields.CATEGORY,"categorys"

        ), CategoryRequest.class);

        register("size", sizeController, sizeService.init(
                context.getBean(SizeRepository.class),
                context.getBean(SizeMapper.class),
                SearchFields.SIZE,"sizes"

        ), SizeRequest.class);

        register("color", colorController, colorService.init(
                context.getBean(ColorRepository.class),
                context.getBean(ColorMapper.class),
                SearchFields.COLOR,"colors"

        ), ColorRequest.class);

        //guarantee
        register("guarantee", guaranteeController, guaranteeService.init(
                context.getBean(GuaranteeRepository.class),
                context.getBean(GuaranteeMapper.class),
                SearchFields.GUARANTEE,"guarantees"

        ), GuaranteeRequest.class);

        // product detail
        register("product-detail", productDetailController, productDetailService.init(
                context.getBean(ProductDetailRepository.class),
                context.getBean(ProductDetailMapper.class),
                SearchFields.PRODUCT_DETAIL,"productDetails"
        ), ProductDetailRequest.class);

        // address
        register("province", provinceController, provinceService.init(
                context.getBean(ProvinceRepository.class),
                context.getBean(ProvinceMapper.class),
                SearchFields.PROVINCE,"provinces"
        ), ProvinceRequest.class);

        register("district", districtController, districtService.init(
                context.getBean(DistrictRepository.class),
                context.getBean(DistrictMapper.class),
                SearchFields.DISTRICT,"districts"
        ), DistrictRequest.class);

        register("ward", wardController, wardService.init(
                context.getBean(WardRepository.class),
                context.getBean(WardMapper.class),
                SearchFields.WARD,"wards"

        ), WardRequest.class);

        register("address", addressController, addressService.init(
                context.getBean(AddressRepository.class),
                context.getBean(AddressMapper.class),
                SearchFields.WARD,"address"
        ), AddressRequest.class);
    }

    private <I, O> void register(String resource,
                                 GenericController<I, O> controller,
                                 CrudService<Long, I, O> service,
                                 Class<I> requestType
    ) throws NoSuchMethodException {
        RequestMappingInfo.BuilderConfiguration options = new RequestMappingInfo.BuilderConfiguration();
        options.setPatternParser(new PathPatternParser());

        controller.setCrudService(service);
        controller.setRequestType(requestType);

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource)
                        .methods(RequestMethod.GET)
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("getAllResources", int.class, int.class,
                        String.class, String.class, String.class, boolean.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource + "/{id}")
                        .methods(RequestMethod.GET)
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("getResource", Long.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource)
                        .methods(RequestMethod.POST)
                        .consumes(MediaType.APPLICATION_JSON_VALUE)
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("createResource", JsonNode.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource + "/{id}")
                        .methods(RequestMethod.PUT)
                        .consumes(MediaType.APPLICATION_JSON_VALUE)
                        .produces(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("updateResource", Long.class, JsonNode.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource + "/{id}")
                        .methods(RequestMethod.DELETE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("deleteResource", Long.class)
        );

        handlerMapping.registerMapping(
                RequestMappingInfo.paths("/api/" + resource)
                        .methods(RequestMethod.DELETE)
                        .consumes(MediaType.APPLICATION_JSON_VALUE)
                        .options(options)
                        .build(),
                controller,
                controller.getClass().getMethod("deleteResources", List.class)
        );
    }
}