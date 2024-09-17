package com.example.demo.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClientGioHangRequest {
    private BigInteger productId;
    private BigInteger romId;
    private BigInteger colorId;
}
