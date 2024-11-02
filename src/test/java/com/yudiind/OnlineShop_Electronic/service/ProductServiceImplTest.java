package com.yudiind.OnlineShop_Electronic.service;

import com.yudiind.OnlineShop_Electronic.model.dto.ProductDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ProductSearchResponse;
import com.yudiind.OnlineShop_Electronic.model.entity.Category;
import com.yudiind.OnlineShop_Electronic.model.entity.Image;
import com.yudiind.OnlineShop_Electronic.model.entity.Product;
import com.yudiind.OnlineShop_Electronic.repository.CategoryRepository;
import com.yudiind.OnlineShop_Electronic.repository.ProductRepository;
import com.yudiind.OnlineShop_Electronic.service.Impl.ProductServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ProductServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void addProduct() throws IOException {

        ProductDTO productDTO = ProductDTO.builder()
                .id(1L)
                .categoryId(1L)
                .name("samsung A55")
                .sku("8/128")
                .price(120F)
                .stock(5)
                .longDesc("new smartphone samsung")
                .build();

        Category category = new Category(2L, "smartphone");

        MockMultipartFile[] file = new MockMultipartFile[1];
        file[0] = new MockMultipartFile ("test", "samsunga55_123.jpg", "image/jpeg", "test image".getBytes());

        Product product = new Product();
        product.setId(productDTO.getId());
        product.setCategory(category);
        product.setName(productDTO.getName());
        product.setSku(productDTO.getSku());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setLongDesc(productDTO.getLongDesc());

        Image image = new Image();
        image.setFileName(file[0].getOriginalFilename());
        image.setProduct(product);

        List<Image> imageList = new ArrayList<>();
        imageList.add(image);

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDTO result = productService.addProduct(productDTO, file);

        verify(categoryRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));

        assertNotNull(result);
        assertEquals(productDTO.getName(), result.getName());
        assertEquals(productDTO.getSku(), result.getSku());
        assertEquals(productDTO.getPrice(), result.getPrice());
    }

    @Test
    void updateProduct() throws IOException{

        Long productId = 4L;
        Category category = new Category(2L, "smartphone");
        ProductDTO productDTO = ProductDTO.builder()
                .id(productId)
                .categoryId(category.getId())
                .sku("8/256")
                .price(180F)
                .build();

        MockMultipartFile[] file = new MockMultipartFile[1];
        file[0] = new MockMultipartFile ("test", "samsunga55_123.jpg", "image/jpeg", "test image".getBytes());

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setCategory(category);
        existingProduct.setSku("8/128");
        existingProduct.setPrice(120F);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(productDTO.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation ->{
            Product updatedProduct = invocation.getArgument(0);
            updatedProduct.setId(productId);
            return updatedProduct;
        });

        ProductDTO result = productService.updateProduct(productId, file, productDTO);

        System.out.println("Product ID: " + result.getId());
        System.out.println("Category ID: " + result.getCategoryId());
        System.out.println("SKU: " + result.getSku());
        System.out.println("Price: " + result.getPrice());
        System.out.println("Image: " + existingProduct.getImages().size());

        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals(category.getId(), result.getCategoryId());
        assertEquals(productDTO.getSku(), result.getSku());
        assertEquals(productDTO.getPrice(), result.getPrice());

        // Assert the images
        assertNotNull(existingProduct.getImages());
        assertEquals(1, existingProduct.getImages().size());
        assertEquals("samsunga55_123.jpg", existingProduct.getImages().get(0).getFileName());

        verify(productRepository, times(1)).findById(productId);
        verify(categoryRepository, times(1)).findById(productDTO.getCategoryId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testSearchProduct() {

        String keyword = "iphone";
        Integer page = 1;
        Integer size = 2;

        List<Product> productList = Stream.generate(Product::new)
                .limit(4)
                .collect(Collectors.toList());

        when(productRepository.findAllByNameContainingIgnoreCase(keyword, PageRequest.of(page, size))).thenReturn(productList);

        List<ProductSearchResponse> responses = productService.searchProduct(keyword, page, size);

        assertNotNull(responses);
        assertEquals(responses.size(), productList.size());

        verify(productRepository, times(1)).findAllByNameContainingIgnoreCase(keyword, PageRequest.of(page, size));

    }

    @Test
    void testGetProductByFilter() {

        Integer page = 1;
        Integer size = 2;

        String sort = Math.random() < 0.5 ? "lowest" : "highest";
        String category = "smartphone";
        Float mixPrice = 100F;
        Float maxPrice = mixPrice + 50F;

        Product product = new Product();
        product.setImages(new ArrayList<>());
        product.setName("Test Product");

        List<Product> productList = new ArrayList<>();
        productList.add(product);

        Page<Product>  productPage = new PageImpl<>(productList);

        when(productRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(productPage);

        List<ProductSearchResponse> response = productService.getProductByFilter(page, size, sort, category, mixPrice, maxPrice);

        assertNotNull(response);
        assertEquals(response.size(), productList.size());
        response.forEach(responseDTO ->
                assertEquals(responseDTO.getName(), product.getName() ));
    }
}
