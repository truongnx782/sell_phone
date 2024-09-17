package com.example.demo.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailResponse {
    private BigInteger id;
    private String tenSanPham;
    private BigDecimal giaBan;
    private List<String> imeiList;


}

