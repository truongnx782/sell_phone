package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

@Entity
@Table(name = "SanPham")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SanPham {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "MaSanPham", unique = true)
    private String maSanPham;

    @Column(name = "TenSanPham")
    private String tenSanPham;

    @ManyToOne
    @JoinColumn(name = "IDHeDieuHanh")
    private HeDieuHanh idHeDieuHanh;

    @ManyToOne
    @JoinColumn(name = "IDManHinh")
    private ManHinh idManHinh;

    @ManyToOne
    @JoinColumn(name = "IDHang")
    private Hang idHang;

    @ManyToOne
    @JoinColumn(name = "IDCameraTruoc")
    private CameraTruoc idCameraTruoc;

    @ManyToOne
    @JoinColumn(name = "IDCameraSau")
    private CameraSau idCameraSau;

    @ManyToOne
    @JoinColumn(name = "IDSim")
    private Sim idSim;

    @ManyToOne
    @JoinColumn(name = "IDPin")
    private Pin idPin;

    @ManyToOne
    @JoinColumn(name = "IDChip")
    private Chip idChip;

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

}
