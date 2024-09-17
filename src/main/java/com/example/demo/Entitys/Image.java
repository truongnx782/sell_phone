package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Table(name = "IMAGE")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Image {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "SanPham_ID")
    private SanPham sanPhamId;

    @Column(name = "TenAnh")
    private String tenAnh;

    @Column(name = "MauSacId")
    private BigInteger mauSacId;

    @Column(name = "TrangThai")
    private Integer trangThai;

}
