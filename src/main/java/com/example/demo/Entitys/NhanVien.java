package com.example.demo.Entitys;

//<<<<<<< HEAD
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//
//=======
import java.math.BigInteger;
import java.util.Date;
import jakarta.persistence.*;
//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "NhanVien")
public class NhanVien {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigInteger id;

    @Column(name = "MaNhanVien")
    private String maNhanVien;

    @Column(name = "TenNhanVien")
    private String tenNhanVien;

    @Column(name = "SDT")
    private String sdt;

    @Column(name = "Email")
    private String email;

    @Column(name = "DiaChi")
    private String diaChiCuThe;

    @Column(name = "Phuong")
    private String phuong;

    @Column(name = "Quan")
    private String quan;

    @Column(name = "Tinh")
    private String tinh;

    @Column(name = "CCCD")
    private String CCCD;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "NgaySinh")
    private Date ngaySinh;

    @Column(name = "AnhNhanVien")
    private String anhNhanVien;

    @Column(name = "MatKhau")
    private String matKhau;

    @Column(name = "GioiTinh")
    private Boolean gioiTinh;

    @Column(name = "GhiChu")
    private String ghiChu;

    @Column(name = "NgayTao")
    private Date ngayTao;

    @Column(name = "NgaySua")
    private Date ngaySua;

    @Column(name = "NguoiTao")
    private String nguoiTao;

    @Column(name = "NguoiSua")
    private String nguoiSua;

    @Column(name = "TrangThai")
    private Integer trangThai;
////<<<<<<< HEAD
//    @ManyToOne
//    @JoinColumn(name = "ChucVu_ID")
//    private ChucVu idChucVu;
////=======
    @Column(name = "ChucVu_ID")
    private BigInteger idChucVu;

//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22

}

