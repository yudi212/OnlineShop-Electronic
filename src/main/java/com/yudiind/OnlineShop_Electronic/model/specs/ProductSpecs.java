package com.yudiind.OnlineShop_Electronic.model.specs;

import com.yudiind.OnlineShop_Electronic.model.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecs {

    public static Specification<Product> withCategory(String category){
        return (root, query, cb) -> {
           if (category == null){
               return cb.isTrue(cb.literal(true));
           }
           return cb.equal(root.get("category").get("name"), category);
        };
    }

    public static Specification<Product> minPrice(Float price){
        return (root, query, cb) -> {
            if (price == null){
                return cb.isTrue(cb.literal(true));
            }
            return cb.greaterThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Product> maxPrice(Float price){
        return (root, query, cb) -> {
          if (price == null){
              return cb.isTrue(cb.literal(true));
          }
          return cb.lessThan(root.get("price"), price);
        };
    }
}
