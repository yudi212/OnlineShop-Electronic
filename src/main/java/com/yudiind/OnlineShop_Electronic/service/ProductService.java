package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.ProductDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ProductSearchResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    ProductDTO addProduct(ProductDTO productDTO, MultipartFile[] file) throws IOException;

    ProductDTO updateProduct(Long id, MultipartFile[] files, ProductDTO productDTO) throws IOException;

    List<ProductSearchResponse> searchProduct(String keyword, Integer page, Integer size);

    List<ProductSearchResponse> getProductByFilter(Integer page, Integer size, String sort, String category, Float minPrice, Float maxPrice);
    ProductDTO getProductById(Long id);

    void deleteById(Long id);

}
