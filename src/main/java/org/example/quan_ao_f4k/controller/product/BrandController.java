//package org.example.quan_ao_f4k.controller.product;
//
//import jakarta.annotation.Nullable;
//import lombok.AllArgsConstructor;
//import org.example.quan_ao_f4k.dto.request.product.BrandRequest;
//import org.example.quan_ao_f4k.service.product.BrandServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//@RequestMapping(value = "brands")
//@RestController()
//@AllArgsConstructor
//public class BrandController {
//    private BrandServiceImpl brandService;
//
//
////    @GetMapping("")
////    public ResponseEntity<?> get(
////            @RequestParam(name = "page", defaultValue = "1") int page,
////            @RequestParam(name = "size", defaultValue = "20") int size,
////            @RequestParam(name = "sort", defaultValue = "id,desc") String sort,
////            @RequestParam(name = "filter", required = false) @Nullable String filter,
////            @RequestParam(name = "search", required = false) @Nullable String search,
////            @RequestParam(name = "all", required = false) boolean all
////    ) {
////        return ResponseEntity.ok().body(brandService.findAll(page,size,sort,filter,search,all));
////
////    }
////
////    @PostMapping("")
////    public ResponseEntity<?> get(@RequestBody BrandRequest request){
////
////        return ResponseEntity.ok().body(brandService.save(request));
////
////    }
////    @PutMapping("/{id}")
////    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BrandRequest request) {
////        return ResponseEntity.ok().body(brandService.save(id, request));
////    }
////
////    @DeleteMapping("/{id}")
////    public ResponseEntity<?> delete(@PathVariable Long id) {
////        brandService.delete(id);
////        return ResponseEntity.noContent().build();
////    }
//
////    @Autowired
////    private RestTemplate restTemplate;
////
////    @GetMapping("/admin/brand-management")
////    public String showBrandManagementPage(Model model) {
////        String apiUrl = "http://localhost:8080/api/brand"; // Đường dẫn tới API getAllResources
////
////        // Gọi API và nhận dữ liệu
////        BrandResponse[] brandArray = restTemplate.getForObject(apiUrl, BrandResponse[].class);
////        List<BrandResponse> brands = Arrays.asList(brandArray);
////
////        // Truyền dữ liệu vào model để hiển thị trong giao diện Thymeleaf
////        model.addAttribute("brands", brands);
////
////        return "admin/brand-management";  // Trả về file HTML trong thư mục templates/admin/brand-management.html
////    }
//}
