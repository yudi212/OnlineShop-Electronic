package com.yudiind.OnlineShop_Electronic.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "product")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    @Column(length = 50000000)
    private byte[] imageBytes;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
