package com.example.demo.Beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class HoaDonBean {
    private String tenTrangThai;
    private Integer soHoaDon;
    private BigDecimal doanhThu;
}
