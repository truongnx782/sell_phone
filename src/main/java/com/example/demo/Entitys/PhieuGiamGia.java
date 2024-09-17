package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PhieuGiamGia")
public class PhieuGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigInteger id;

    @Column(name = "Ma_VC")
    private String maPhieuGiamGia;

    @Column(name = "Ten_VC")
    private String tenPhieuGiamGia;

    @Column(name = "GiaTriToiThieuApDung")
    private BigDecimal giaTriToiThieuApDung;

    @Column(name = "MoTa")
    private String moTa;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "PhanTramGiam")
    private Integer phanTramGiam;

    @Column(name = "SoTienDuocGiamToiDa")
    private BigDecimal soTienDuocGiamToiDa;

    @Column(name = "NgayBatDau")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngayBatDau;

    @Column(name = "NgayKetThuc")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngayKetThuc;

    @Column(name = "SoTienDuocGiam")
    private BigDecimal soTienDuocGiam;

    @Column(name = "LoaiGiamGia")
    private Integer loaiGiamGia;

    @Column(name = "NgayTao")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngayTao;

    @Column(name = "NgaySua")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngaySua;

    @Column(name = "NguoiTao")
    private String nguoiTao;

    @Column(name = "NguoiSua")
    private String nguoiSua;

    @Column(name = "TrangThai")
    private Integer trangThai;

    @Column(name = "TrangThaiSoLuong")
    private Integer trangThaiSoLuong;

    @Column(name = "TrangThaiCongKhai")
    private Integer trangThaiCongKhai;

    @Override
    public String toString() {
        return "PhieuGiamGia{" +
                "id=" + id +
                ", maPhieuGiamGia='" + maPhieuGiamGia + '\'' +
                ", tenPhieuGiamGia='" + tenPhieuGiamGia + '\'' +
                ", GiaTriToiThieuApDung=" + giaTriToiThieuApDung +
                ", moTa='" + moTa + '\'' +
                ", soLuong=" + soLuong +
                ", phanTramGiam=" + phanTramGiam +
                ", soTienDuocGiamToiDa=" + soTienDuocGiamToiDa +
                ", ngayBatDau=" + ngayBatDau +
                ", ngayKetThuc=" + ngayKetThuc +
                ", soTienDuocGiam=" + soTienDuocGiam +
                ", loaiGiamGia='" + loaiGiamGia + '\'' +
                ", ngayTao=" + ngayTao +
                ", ngaySua=" + ngaySua +
                ", nguoiTao='" + nguoiTao + '\'' +
                ", nguoiSua='" + nguoiSua + '\'' +
                ", trangThai=" + trangThai +
                '}';
    }

}
