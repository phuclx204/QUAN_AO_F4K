package org.example.quan_ao_f4k.controller.product;

import lombok.SneakyThrows;
import org.example.quan_ao_f4k.controller.GenericController;
import org.example.quan_ao_f4k.dto.request.product.ColorRequest;
import org.example.quan_ao_f4k.dto.response.product.ColorResponse;
import org.example.quan_ao_f4k.service.product.ColorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "admin/color")
@Controller
public class ColorController {

    @Autowired
    private ColorServiceImpl colorService;

    @Autowired
    private GenericController<ColorResponse, ColorRequest> controller;


    @GetMapping
    public String getColor() {
        return "admin/product/color";
    }

    @SneakyThrows
    @PostMapping
    @ResponseBody
    public ResponseEntity<ColorResponse> save(@RequestBody ColorRequest request) {
        return ResponseEntity.ok(colorService.save(request));
    }

    @SneakyThrows
    @DeleteMapping
    @ResponseBody
    public ResponseEntity<Void> delete(@RequestParam("id") Long id) {
        colorService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @SneakyThrows
    @PutMapping
    @ResponseBody
    public ResponseEntity<ColorResponse> update(@RequestBody ColorRequest request) {
        return ResponseEntity.ok(colorService.save(request.getId(), request));
    }
}
