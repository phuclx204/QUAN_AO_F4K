package org.example.quan_ao_f4k.controller;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.example.quan_ao_f4k.dto.request.address.AddressRequest;
import org.example.quan_ao_f4k.dto.request.address.DistrictRequest;
import org.example.quan_ao_f4k.dto.request.address.ProvinceRequest;
import org.example.quan_ao_f4k.dto.request.address.WardRequest;
import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
import org.example.quan_ao_f4k.dto.request.product.ColorRequest;
import org.example.quan_ao_f4k.dto.request.product.SizeRequest;
import org.example.quan_ao_f4k.dto.response.address.AddressResponse;
import org.example.quan_ao_f4k.dto.response.address.DistrictResponse;
import org.example.quan_ao_f4k.dto.response.address.ProvinceResponse;
import org.example.quan_ao_f4k.dto.response.address.WardResponse;
import org.example.quan_ao_f4k.dto.response.product.BrandResponse;
import org.example.quan_ao_f4k.dto.response.product.CategoryResponse;
import org.example.quan_ao_f4k.dto.response.product.ColorResponse;
import org.example.quan_ao_f4k.dto.response.product.SizeResponse;
import org.example.quan_ao_f4k.mapper.product.BrandMapper;
import org.example.quan_ao_f4k.mapper.product.CategoryMapper;
import org.example.quan_ao_f4k.mapper.product.ColorMapper;
import org.example.quan_ao_f4k.mapper.product.SizeMapper;
import org.example.quan_ao_f4k.model.product.Brand;
import org.example.quan_ao_f4k.model.product.Category;
import org.example.quan_ao_f4k.model.product.Color;
import org.example.quan_ao_f4k.model.product.Size;
import org.example.quan_ao_f4k.repository.product.BrandRepository;
import org.example.quan_ao_f4k.repository.product.CategoryRepository;
import org.example.quan_ao_f4k.repository.product.ColorRepository;
import org.example.quan_ao_f4k.repository.product.SizeRepository;
import org.example.quan_ao_f4k.service.CrudService;
import org.example.quan_ao_f4k.service.GenericService;
import org.example.quan_ao_f4k.service.address.AddressServiceImpl;
import org.example.quan_ao_f4k.service.address.DistrictServiceImpl;
import org.example.quan_ao_f4k.service.address.ProvinceServiceImpl;
import org.example.quan_ao_f4k.service.address.WardServiceImpl;
import org.example.quan_ao_f4k.service.product.CategoryServiceImpl;
import org.example.quan_ao_f4k.service.product.ColorServiceImpl;
import org.example.quan_ao_f4k.service.product.SizeServiceImpl;
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

    private GenericController<ProvinceRequest, ProvinceResponse> provinceController;
    private GenericController<DistrictRequest, DistrictResponse> districtController;
    private GenericController<WardRequest, WardResponse> wardController;
    private GenericController<AddressRequest, AddressResponse> addressController;

    private GenericService<Brand, BrandRequest, BrandResponse> brandService;

    @PostConstruct
    public void registerControllers() throws NoSuchMethodException {

        // product
        register("category", categoryController,context.getBean(CategoryServiceImpl.class)
        , CategoryRequest.class);

        register("size", sizeController,context.getBean(SizeServiceImpl.class)
                , SizeRequest.class);

        register("color", colorController,context.getBean(ColorServiceImpl.class)
                , ColorRequest.class);

        register("brand", brandController, brandService.init(
                context.getBean(BrandRepository.class),
                context.getBean(BrandMapper.class),
                SearchFields.BRAND,"brands"

        ), BrandRequest.class);

        // address
        register("province", provinceController,context.getBean(ProvinceServiceImpl.class)
                , ProvinceRequest.class);
        register("district", districtController,context.getBean(DistrictServiceImpl.class)
                , DistrictRequest.class);
        register("ward", wardController,context.getBean(WardServiceImpl.class)
                , WardRequest.class);
        register("address", addressController,context.getBean(AddressServiceImpl.class)
                , AddressRequest.class);

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
