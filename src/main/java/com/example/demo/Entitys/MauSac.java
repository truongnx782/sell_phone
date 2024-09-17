package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Table(name = "MauSac")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MauSac {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "MaMauSac")
    private String maMauSac;

    @Column(name = "TenMauSac")
    private String tenMauSac;

    @Column(name = "TrangThai")
    private Integer trangThai;

}
