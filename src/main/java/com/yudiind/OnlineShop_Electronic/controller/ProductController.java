package com.yudiind.OnlineShop_Electronic.controller;

import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.model.dto.ProductDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ProductSearchResponse;
import com.yudiind.OnlineShop_Electronic.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @PostMapping(value = "/add")
    public ResponseEntity<?> addProduct(@RequestPart("file") MultipartFile[] files,
                                        @ModelAttribute @Valid ProductDTO productDTO,
                                        BindingResult bindingResult) throws IOException {

        if (bindingResult.hasErrors()){
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError ->
                    errors.put(fieldError.getField(),
                            fieldError.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }

        ProductDTO product = productService.addProduct(productDTO, files);
        return ResponseEntity.ok().body(product);
    }

    @PatchMapping("/update/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("productId") Long productId,
                                                    @RequestPart(value = "files", required = false) MultipartFile[] files,
                                                    @ModelAttribute("productDTO") ProductDTO productDTO) throws IOException{

        ProductDTO product = productService.updateProduct(productId, files, productDTO);
        return ResponseEntity.ok().body(product);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductSearchResponse>> searchProduct(@RequestParam("keyword") String keyword,
                                                                     @RequestParam("page") Integer page,
                                                                     @RequestParam("size") Integer size){

        List<ProductSearchResponse> responses = productService.searchProduct(keyword, page, size);
        return ResponseEntity.ok().body(responses);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductSearchResponse>> getProductByFilter(@RequestParam("page") Integer page,
                                                                          @RequestParam("size") Integer size,
                                                                          @RequestParam(value = "sort", required = false) String sort,
                                                                          @RequestParam(value = "category", required = false) String category,
                                                                          @RequestParam(value = "minPrice", required = false) Float minPrice,
                                                                          @RequestParam(value = "maxPrice", required = false) Float maxPrice){

        if (Objects.isNull(page) || page < 0){
            throw new InvalidArgumentException("Invalid page");
        }
        if (Objects.isNull(size) || size < 0){
            throw new InvalidArgumentException("Invalid pageSize");
        }

        List<ProductSearchResponse> responses = productService.getProductByFilter(page, size, sort,category, minPrice, maxPrice);
        return ResponseEntity.ok().body(responses);
    }


}
