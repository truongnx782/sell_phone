package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ChucVu")
public class ChucVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private BigInteger id;
    @Column(name = "MaChucVu")
    private String maChucVu;
    @Column(name = "TenChucVu")
    private String tenChucVu;
    @Column(name = "TrangThai")
    private Integer trangThai;

}
