package com.example.demo.Entitys;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigInteger;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "KhachHang")
public class KhachHang {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "MaKhachHang")
    private  String maKhachHang;

    @Column(name = "TenKhachHang")
    private String tenKhachHang;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "Email")
    private String email;

    @Column(name = "MatKhau")
    private String matKhau;


    @Column(name = "TrangThai")
    private Integer trangThai;

    @Column(name = "AnhKhachHang")
    private String anhKhachHang;

    public KhachHang(String sdt) {
        this.sdt = sdt;
    }

}
