package com.example.demo.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LichSuHoaDonRequest {
    private BigInteger idHoaDon;
    private BigInteger idNhanVien;
    private BigInteger idKhachHang;
    private Date thoiGian;
    private String ghiChu;
    private Integer trangThai;

}
