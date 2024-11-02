package com.yudiind.OnlineShop_Electronic.service.Impl;


import com.yudiind.OnlineShop_Electronic.error.exception.InvalidArgumentException;
import com.yudiind.OnlineShop_Electronic.error.exception.ResourceNotFoundException;
import com.yudiind.OnlineShop_Electronic.model.dto.ProductDTO;
import com.yudiind.OnlineShop_Electronic.model.dto.ProductSearchResponse;
import com.yudiind.OnlineShop_Electronic.model.entity.Category;
import com.yudiind.OnlineShop_Electronic.model.entity.Image;
import com.yudiind.OnlineShop_Electronic.model.entity.Product;
import com.yudiind.OnlineShop_Electronic.model.specs.ProductSpecs;
import com.yudiind.OnlineShop_Electronic.repository.CategoryRepository;
import com.yudiind.OnlineShop_Electronic.repository.ImageRepository;
import com.yudiind.OnlineShop_Electronic.repository.ProductRepository;
import com.yudiind.OnlineShop_Electronic.service.ProductService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ImageRepository imageRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.imageRepository = imageRepository;
        this.categoryRepository = categoryRepository;
    }
    private final String UPLOAD_FOLDER = "C:\\Users\\Windows 10\\Pictures\\Online Shop Electronic";

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, MultipartFile[] files) throws IOException {

        Product product = fromProductDTO(productDTO);

        List<Image> images = new ArrayList<>();

        for (MultipartFile image : files ){
            if (image.isEmpty()){
                return null;
            }
            String fileName = image.getOriginalFilename();

            Image imageUpload = new Image();
            imageUpload.setFileName(fileName);
            imageUpload.setImageBytes(image.getBytes());
            imageUpload.setProduct(product);


            // - Path => direktori pada sistem file yg digunakan untuk memanipulasi dan memeriksa lokasi file atau direktori di sistem file.
            // - Paths =>  utility class (kelas pembantu) untuk membuat atau menginisialisasi objek Path.
            //   Paths menyediakan metode statis get() untuk mengonversi String (atau URI) menjadi objek Path. Itulah fungsi utama Paths.
            // - Create a Path object
            Path path = Paths.get(UPLOAD_FOLDER, fileName);
            // save image to file system
            Files.write(path, image.getBytes());

            images.add(imageUpload);
        }

        product.setImages(images);
        Product savedProduct = productRepository.save(product);
        log.info("Produt saved successfully");
        return fromProduct(savedProduct);
    }

    @Override
    public ProductDTO updateProduct(Long id, MultipartFile[] files, ProductDTO productDTO) throws IOException{

        // karna metode update nya Patch => maka bisa melakukan update data hanya pada field yang dikirimkan saja,
        //                                  tanpa harus mengirimkan semua data termasuk files gambar jika tidak diperlukan.

        // oleh karena itu di Controller di @RequestPart(value = "files", required = false) MultipartFile[] files.
        // Dengan required = false pada @RequestPart, files tidak perlu dikirimkan jika Anda hanya ingin mengupdate field lainnya.


       Product existingProduct = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with Id :" + id));

       Category category = categoryRepository.findById(productDTO.getCategoryId())
               .orElseThrow(()-> new ResourceNotFoundException("Category not found with Id :" + productDTO.getCategoryId()));

       // if (files == null){
       //    return null;
       // }

       // - file.length > 0 berarti memastikan bahwa ada setidaknya satu file yang diunggah oleh pengguna.
       //   Jika nilai file.length lebih besar dari 0, artinya ada file yang diunggah.
       // - !file[0].isEmpty() memastikan bahwa file pertama dalam array tidak kosong,
       //   yaitu bahwa ada data atau konten yang diunggah untuk file tersebut.
       if (files != null && files.length > 0 && !files[0].isEmpty()){
           List<Image> existingImage = existingProduct.getImages();
           if (existingImage != null && !existingImage.isEmpty()){
               for (Image file : existingImage){
                   Files.deleteIfExists(Paths.get(UPLOAD_FOLDER, file.getFileName()));
               }
           }

           List<Image> imageModel = new ArrayList<>();
           for (MultipartFile file : files){
               Image newImage = new Image();
               String fileName = file.getOriginalFilename();
               newImage.setFileName(fileName);
               newImage.setImageBytes(file.getBytes());
               newImage.setProduct(existingProduct);

               Path filePath = Paths.get(UPLOAD_FOLDER, fileName);
               Files.write(filePath, file.getBytes());

               imageModel.add(newImage);
           }
           existingProduct.setImages(imageModel);
       }

       // // Update field lainnya
        updateProductField(existingProduct, productDTO);
        Product productUpdate = productRepository.save(existingProduct);
        log.info("Product updated successfully");
        return fromProduct(productUpdate);
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product not found with Id :" + id));
        return fromProduct(product);
    }

    @Override
     public List<ProductSearchResponse> searchProduct(String keyword, Integer page, Integer size){
        if (Objects.isNull(page) || Objects.isNull(size)){
            throw new InvalidArgumentException("Page and size are required");
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        List<Product> products = productRepository.findAllByNameContainingIgnoreCase(keyword, pageRequest);
        if (products.isEmpty()){
            throw new ResourceNotFoundException("Product not found");
        }
        List<ProductSearchResponse> productSearchResponse = new ArrayList<>();

        for (Product product : products){
            ProductSearchResponse response = new ProductSearchResponse();
            response.setName(product.getName());
            response.setPrice(product.getPrice());

            // Ambil gambar pertama dari list gambar produk
            if (product.getImages() != null && !product.getImages().isEmpty()){
                Image firstImage = product.getImages().get(0);  // Ambil gambar pertama
                response.setImages(firstImage.getFileName());   // atau firstImage.getImageBytes() sesuai dengan kebutuhan
            }
            productSearchResponse.add(response);
        }
        return productSearchResponse;
    }

    @Override
    public List<ProductSearchResponse> getProductByFilter(Integer page, Integer size, String sort, String category, Float minPrice, Float maxPrice){

        log.info("Filtering products with category: {}, minPrice: {}, maxPrice: {}", category, minPrice, maxPrice);
        PageRequest pageRequest;
        if (Objects.nonNull(sort) && !sort.isBlank()){
            Sort sortRequest = getSort(sort);
            if (Objects.isNull(sortRequest)){
                throw new InvalidArgumentException("Invalid sort Parameter");
            }
            pageRequest = PageRequest.of(page, size, sortRequest);
        } else {
            pageRequest = PageRequest.of(page, size);
        }

        Specification<Product> combination =
                Objects.requireNonNull(Specification.where(ProductSpecs.withCategory(category)))
                        .and(ProductSpecs.minPrice(minPrice))
                        .and(ProductSpecs.maxPrice(maxPrice));
//                (root,query, cb) -> cb.isTrue(cb.literal(true));

        Page<Product> searchResponse =  productRepository.findAll(combination, pageRequest);
//        List<ProductSearchResponse> productSearchResponse = new ArrayList<>();

         return searchResponse.stream()
                 // Mapping Product entities to ProductSearchResponse DTOs
                 .map(product -> {
                     ProductSearchResponse response = new ProductSearchResponse();
                     response.setName(product.getName());
                     response.setPrice(product.getPrice());

                     // Pastikan untuk memeriksa apakah images tidak kosong
                     if (!product.getImages().isEmpty()){
                         Image images = product.getImages().get(0);
                         response.setImages(images.getFileName());
                     } else {
                         response.setImages(null);      // Atau set nilai default jika tidak ada gambar
                     }
                     return response;
                     })
                 .collect(Collectors.toList());
    }


    @Override
    public void deleteById(Long id) {

    }

    private Sort getSort(String sort){
        switch (sort){
            case "lowest":
                return Sort.by(Sort.Direction.ASC, "price");
            case "highest":
                return Sort.by(Sort.Direction.DESC, "price");
            default:
                return null;
        }
    }

    private void updateProductField(@NotNull Product product, @NotNull ProductDTO productDTO){
        if (Objects.nonNull(productDTO.getName())){
            product.setName(productDTO.getName());
        }
        if (Objects.nonNull(productDTO.getSku())){
            product.setSku(productDTO.getSku());
        }
        if (Objects.nonNull(productDTO.getPrice())){
            product.setPrice(productDTO.getPrice());
        }
        if (Objects.nonNull(productDTO.getStock())){
            product.setStock(productDTO.getStock());
        }
        if (Objects.nonNull(productDTO.getLongDesc())){
            product.setLongDesc(productDTO.getLongDesc());
        }
    }

    public Product fromProductDTO(ProductDTO productDTO){
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(()-> new ResourceNotFoundException("Category not found"));

        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setCategory(category);
        product.setSku(productDTO.getSku());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setLongDesc(productDTO.getLongDesc());
        return product;
    }

    public ProductDTO fromProduct(Product product){
        return ProductDTO.builder()
                .id(product.getId())
                .categoryId(product.getCategory() != null ? product.getCategory().getId() : null)
                .name(product.getName())
                .sku(product.getSku())
                .price(product.getPrice())
                .stock(product.getStock())
                .longDesc(product.getLongDesc())
                .build();
    }


}
