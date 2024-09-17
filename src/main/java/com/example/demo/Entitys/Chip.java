package com.example.demo.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Table(name = "Chip")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Chip {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "Ten")
    private String ten;

    @Column(name = "TrangThai")
    private Integer trangThai;

}
