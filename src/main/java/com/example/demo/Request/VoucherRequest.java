package com.example.demo.Request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
public class VoucherRequest {
    private BigDecimal totalAmount;
    private BigInteger khachHangId;


}
