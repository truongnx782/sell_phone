package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ChiTietSanPham")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private BigInteger id;

    @Column(name = "MaChiTietSanPham", unique = true)
    private String maChiTietSanPham;

    @ManyToOne
    @JoinColumn(name = "IDMauSac")
    private MauSac idMauSac;

    @ManyToOne
    @JoinColumn(name = "IDROM")
    private ROM idROM;

    @ManyToOne
    @JoinColumn(name = "IDRAM")
    private RAM idRAM;

    @ManyToOne
    @JoinColumn(name = "IDSanPham")
    private SanPham idSanPham;

    @Column(name = "GiaBan")
    private BigDecimal giaBan;

    @Column(name = "Thue")
    private Integer thue;

    @Column(name = "YeuThich")
    private Integer yeuThich;

    @Column(name = "NgayTao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngayTao;

    @Column(name = "NgaySua")
    @Temporal(TemporalType.TIMESTAMP)
    private Date ngaySua;

    @Column(name = "NguoiTao")
    private String nguoiTao;

    @Column(name = "NguoiSua")
    private String nguoiSua;

    @Column(name = "TrangThai")
    private Integer trangThai;
    @OneToMany(mappedBy = "idChiTietSP")
    private List<Imei> imeiList;

}
