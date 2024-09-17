package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Table(name = "ImeiDaBan")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImeiDaBan {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "IDHoaDonCT")
    private HoaDonChiTiet idHoaDonCT;

    @Column(name = "Ma_Imei")
    private String maImei;


    @Column(name = "TrangThai")
    private Integer trangThai;
}
