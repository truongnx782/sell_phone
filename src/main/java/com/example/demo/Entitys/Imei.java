package com.example.demo.Entitys;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;

@Table(name = "Imei")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Imei {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @ManyToOne
    @JoinColumn(name = "IDChiTietSP")
    @JsonBackReference
    private ChiTietSanPham idChiTietSP;

    @Column(name = "Ma_Imei")
    private String maImei;

    @Column(name = "TrangThai")
    private int trangThai;
}
