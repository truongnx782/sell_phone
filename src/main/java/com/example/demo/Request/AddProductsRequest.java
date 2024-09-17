package com.example.demo.Request;

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
public class AddProductsRequest {
    private List<String> imeiList;
    private BigInteger gioHangId;
    private BigDecimal tongTien;
    private BigDecimal tongTienSG;
}
