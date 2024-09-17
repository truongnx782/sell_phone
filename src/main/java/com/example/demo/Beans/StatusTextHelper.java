package com.example.demo.Beans;

import org.springframework.stereotype.Component;

@Component
public class StatusTextHelper {
    public String statusText(Integer status) {
        switch (status) {
            case 1:
                return "Đặt hàng thành công";
            case 2:
                return "Chờ giao hàng";
            case 3:
                return "Đã giao cho đơn vị vận chuyển";
            case 4:
                return "Giao hàng thành công";
            case 5:
                return "Hoàn tác";
            case 6:
                return "Đã hủy";
            case 7:
                return "Cập nhật đơn hàng";
            case 8:
                return "Đơn hàng đã hoàn thành";  // bán hàng tại quầy
            default:
                return "Trạng thái không xác định";
        }
    }
}
