package com.example.demo.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThongTinNguoiNhanRequest {
    private BigInteger hoaDonId;
    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChi;
}
