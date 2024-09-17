package com.example.demo.Entitys;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
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
@Entity
@Table(name = "HoaDonChiTiet")
public class HoaDonChiTiet {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @ManyToOne
    @JoinColumn(name = "IDChiTietSP")
    private ChiTietSanPham idChiTietSP;
    @ManyToOne
    @JoinColumn(name = "IDHoaDon")
    private HoaDon idHoaDon;
    @Column(name = "Gia")
    private BigDecimal gia;
    @Column(name = "TongTien")
    private BigDecimal tongTien;
    @Column(name = "GhiChu")
    private String ghiChu;
    @Column(name = "TrangThai")
    private Integer trangThai;

}
