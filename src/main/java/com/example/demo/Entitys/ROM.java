package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigInteger;
import java.util.Date;

@Table(name = "ROM")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ROM {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "TenRom")
    private String tenRom;

    @Column(name = "NgayTao")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "DD-MM-YYYY")
    private Date ngayTao;

    @Column(name = "NgaySua")
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(pattern = "DD-MM-YYYY")
    private Date ngaySua;

    @Column(name = "NguoiTao")
    private String nguoiTao;

    @Column(name = "NguoiSua")
    private String nguoiSua;

    @Column(name = "TrangThai")
    private Integer trangThai;

}
