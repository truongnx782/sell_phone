package com.example.demo.Entitys;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamHome {

    private BigInteger idChiTietSP;
    private BigInteger idSanPham;
    private List<BigInteger> idImage;
    private BigInteger idManHinh;
    private String tenSanPham;
    private List<BigInteger> idROM;
    private BigInteger idRAM;
    private BigDecimal giaBan;
    private String tenManHinh;
    private List<String> tenRom;
    private String tenRam;
    private List<String> tenAnh;

    @Override
    public String toString() {
        return "SanPhamHome{" +
                "idChiTietSP=" + idChiTietSP +
                ", idSanPham=" + idSanPham +
                ", idImage=" + idImage +
                ", idManHinh=" + idManHinh +
                ", tenSanPham='" + tenSanPham + '\'' +
                ", idROM=" + idROM +
                ", giaBan=" + giaBan +
                ", tenManHinh='" + tenManHinh + '\'' +
                ", tenRom='" + tenRom + '\'' +
                ", tenAnh='" + tenAnh + '\'' +
                '}';
    }
}
