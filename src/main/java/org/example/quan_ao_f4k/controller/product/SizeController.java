package org.example.quan_ao_f4k.controller.product;

import lombok.SneakyThrows;
import org.example.quan_ao_f4k.dto.request.product.SizeRequest;
import org.example.quan_ao_f4k.dto.response.product.SizeResponse;
import org.example.quan_ao_f4k.service.product.SizeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "admin/size")
@Controller
public class SizeController {
	
	@Autowired
	private SizeServiceImpl sizeService;
	
	@GetMapping
	public String getSize(Model model) {
		return "admin/product/size";
	}

	@SneakyThrows
	@PostMapping
	@ResponseBody
	public ResponseEntity<SizeResponse> save(@RequestBody SizeRequest request) {
		return ResponseEntity.ok(sizeService.save(request));
	}

	@SneakyThrows
	@DeleteMapping
	@ResponseBody
	public ResponseEntity<Void> delete(@RequestParam("id") Long id) {
		sizeService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@SneakyThrows
	@PutMapping
	@ResponseBody
	public ResponseEntity<SizeResponse> update(@RequestBody SizeRequest request) {
		return ResponseEntity.ok(sizeService.save(request.getId(), request));
	}
}
