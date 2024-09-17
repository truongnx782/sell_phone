package com.example.demo.Request;

import com.example.demo.Entitys.KhachHang;
import com.example.demo.Entitys.PhieuGiamGia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDonRequest {
    private KhachHang idKhachHang;
    private PhieuGiamGia idPhieuGiamGia;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private Integer loaiHoaDon;
    private String ghiChu;
    private List<HoaDonChiTietRequest> hoaDonChiTietList;
    private List<ChiTietPhuongThucTTRequest> chiTietPhuongThucTTList;

}
