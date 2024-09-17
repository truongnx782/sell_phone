package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "GioHang")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "KhachHang_ID")
    private KhachHang idKhachHang;

    @Column(name = "TrangThai")
    private Integer trangThai;
}
