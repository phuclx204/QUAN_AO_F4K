//package org.example.quan_ao_f4k.controller.product;
//
//import jakarta.annotation.Nullable;
//import lombok.AllArgsConstructor;
//import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
//import org.example.quan_ao_f4k.dto.request.product.CategoryRequest;
//import org.example.quan_ao_f4k.service.product.CategoryServiceImpl;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RequestMapping(value = "category")
//@RestController()
//@AllArgsConstructor
//public class CategoryController {
//	private CategoryServiceImpl categoryService;
//
//	@GetMapping("")
//	public ResponseEntity<?> get(
//			@RequestParam(name = "page",defaultValue = "1") int page,
//			@RequestParam(name = "size",defaultValue = "20") int size,
//			@RequestParam(name = "sort",defaultValue = "id,desc") String sort,
//			@RequestParam(name = "filter",required = false) @Nullable String filter,
//			@RequestParam(name = "search",required = false) @Nullable String search,
//			@RequestParam(name = "all",required = false) boolean all
//	){
//		return ResponseEntity.ok().body(categoryService.findAll(page,size,sort,filter,search,all));
//	}
//
//	@PostMapping("")
//	public ResponseEntity<?> save(@RequestBody CategoryRequest request){
//		return ResponseEntity.ok().body(categoryService.save(request));
//
//	}
//
//	@PutMapping("/{id}")
//	public ResponseEntity<?> update(@PathVariable Long id,@RequestBody CategoryRequest request){
//		return ResponseEntity.ok().body(categoryService.save(id, request));
//	}
//
//	@DeleteMapping("/{id}")
//	public ResponseEntity<?> delete(@PathVariable Long id){
//		categoryService.delete(id);
//		return ResponseEntity.noContent().build();
//	}
//}
