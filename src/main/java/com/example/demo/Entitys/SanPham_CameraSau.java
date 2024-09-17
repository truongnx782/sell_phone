package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "SanPham_CameraSau")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SanPham_CameraSau {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "SanPhamId")
    private BigInteger sanPhamId;

    @Column(name = "CameraSauId")
    private BigInteger cameraSauId;

    @Column(name = "TrangThai")
    private Integer trangThai;
}
