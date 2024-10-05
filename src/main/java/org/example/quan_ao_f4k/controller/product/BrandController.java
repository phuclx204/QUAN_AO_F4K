package org.example.quan_ao_f4k.controller.product;

//@RestController
//@AllArgsConstructor
//public class BrandController {
//    private BrandServiceImpl brandService;
//
//    @GetMapping("")
//    public ResponseEntity<?> get(
//            @RequestParam(name = "page", defaultValue = "1") int page,
//            @RequestParam(name = "size", defaultValue = "20") int size,
//            @RequestParam(name = "sort", defaultValue = "id,desc") String sort,
//            @RequestParam(name = "filter", required = false) @Nullable String filter,
//            @RequestParam(name = "search", required = false) @Nullable String search,
//            @RequestParam(name = "all", required = false) boolean all
//    ) {
//        return ResponseEntity.ok().body(brandService.findAll(page,size,sort,filter,search,all));
//
//    }
//
//    @PostMapping("")
//    public ResponseEntity<?> get(@RequestBody BrandRequest request){
//
//        return ResponseEntity.ok().body(brandService.save(request));
//
//    }
//    @PutMapping("/{id}")
//    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody BrandRequest request) {
//        return ResponseEntity.ok().body(brandService.save(id, request));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> delete(@PathVariable Long id) {
//        brandService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//
//}
