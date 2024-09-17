package com.example.demo.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ImeiDaBanRequest {
    private BigInteger idHoaDonCT;
    private String maImei;

}

