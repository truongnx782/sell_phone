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
public class RomChiTietSanPham {
    private BigInteger idRom;
    private String tenRom;
    private String tenRam;
    private BigInteger idSanPham;
    private String tenSanPham;
    private List<String> tenAnh;
    private List<BigInteger> idMauSac;
    private List<String> maMauSac;
    private List<String> tenMauSac;
    private String tenMauSP;
    private String anhMSDaiDien;
    private BigDecimal giaBan;

    @Override
    public String toString() {
        return "RomChiTietSanPham{" +
                "idRom=" + idRom +
                ", tenRom='" + tenRom + '\'' +
                ", idSanPham=" + idSanPham +
                ", idMauSac=" + idMauSac +
                ", tenMauSac=" + tenMauSac +
                '}';
    }
}
