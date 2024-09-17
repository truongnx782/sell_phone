package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "LichSuHoaDon")
public class LichSuHoaDon {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @ManyToOne
    @JoinColumn(name = "IDHoaDon")
    private HoaDon idHoaDon;
    @ManyToOne
    @JoinColumn(name = "IDNhanVien")
    private NhanVien idNhanVien;
    @ManyToOne
    @JoinColumn(name = "IDKhachHang")
    private KhachHang idKhachHang;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "ThoiGian")
    private Date thoiGian;
    @Column(name = "GhiChu")
    private String ghiChu;
    @Column(name = "TrangThai")
    private Integer trangThai;


    public LichSuHoaDon(HoaDon idHoaDon, NhanVien idNhanVien, KhachHang idKhachHang, Date thoiGian, String ghiChu, Integer trangThai) {
        this.idHoaDon = idHoaDon;
        this.idNhanVien = idNhanVien;
        this.idKhachHang = idKhachHang;
        this.thoiGian = thoiGian;
        this.ghiChu = ghiChu;
        this.trangThai = trangThai;
    }
}
