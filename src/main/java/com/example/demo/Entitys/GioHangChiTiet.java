package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "GioHangChiTiet")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GioHangChiTiet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "ChiTietSP_ID")
    private ChiTietSanPham idChiTietSanPham;

    @ManyToOne
    @JoinColumn(name = "GioHang_ID")
    private GioHang idGioHang;

    @Column(name = "SoLuong")
    private Integer soLuong;

    @Column(name = "NgayTao")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngayTao;

    @Column(name = "TrangThai")
    private Integer trangThai;
}
