package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "HoaDon")
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "IDKhachHang")
    private KhachHang idKhachHang;

    @ManyToOne
    @JoinColumn(name = "IDNhanVien")
    private NhanVien idNhanVien;

    @ManyToOne
    @JoinColumn(name = "IDPhieuGiamGia")
    private PhieuGiamGia idPhieuGiamGia;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "NgayTao")
    private Date ngayTao;

    @Column(name = "TongTien")
    private BigDecimal tongTien;

    @Column(name = "TongTienSauGiam")
    private BigDecimal tongTienSauGiam;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "NgayThanhToan")
    private Date ngayThanhToan;

    @Column(name = "LoaiHoaDon")
    private Integer loaiHoaDon;

    @Column(name = "TenNguoiNhan")
    private String tenNguoiNhan;

    @Column(name = "SDTNguoiNhan")
    private String sdtNguoiNhan;

    @Column(name = "GhiChu")
    private String ghiChu;

    @Column(name = "MaQR")
    private String maQR;

    @Column(name = "PhiShip")
    private BigDecimal phiShip;

    @Column(name = "DiaChi")
    private String diaChi;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NgayNhanHang")
    private Date ngayNhanHang;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NgayMongMuonNhanHang")
    private Date ngayMongMuonNhanHang;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NgayHuy")
    private Date ngayHuy;

    @Column(name = "TrangThai")
    private Integer trangThai;

    @Column(name = "CoXoa")
    private Integer coXoa;

    @Column(name = "MaHD")
    private String maHD;

    public HoaDon(BigInteger id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "idHoaDon")
    private List<HoaDonChiTiet> hoaDonChiTietList;

    public String getCity() {
        return extractAddressPart(diaChi, 0);
    }

    public String getDistrict() {
        return extractAddressPart(diaChi, 1);
    }

    public String getWard() {
        return extractAddressPart(diaChi, 2);
    }

    public String getSpecificAddress() {
        return extractAddressPart(diaChi, 3);
    }

    private String extractAddressPart(String address, int index) {
        if(address != null){
            String[] parts = address.split(",");
            return parts.length > index ? parts[index].trim() : "";
        }
        return null;
    }

}
