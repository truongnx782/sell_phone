package com.example.demo.Request;

import com.example.demo.Entitys.ChiTietSanPham;
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
public class HoaDonChiTietRequest {
    private ChiTietSanPham idChiTietSP;
    private BigDecimal gia;
    private List<String> imeiList;
    private String ghiChu;

}

