package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate template;

    @MessageMapping("/tabChange")
    public void handleTabChange(TabChangeMessage message) {
        template.convertAndSend("/topic/tabChange", message);
    }
    @MessageMapping("/showPaymentQR")
    @SendTo("/topic/showPaymentQR")
    public PaymentQRMessage sendPaymentQRMessage(PaymentQRMessage message) {
        return message;
    }

    public static class PaymentQRMessage {
        private String hoaDonId;
        private String qrURL;
        private double totalPayAmount;
        private String bankInfo;

        public String getHoaDonId() {
            return hoaDonId;
        }

        public void setHoaDonId(String hoaDonId) {
            this.hoaDonId = hoaDonId;
        }

        public String getQrURL() {
            return qrURL;
        }

        public void setQrURL(String qrURL) {
            this.qrURL = qrURL;
        }

        public double getTotalPayAmount() {
            return totalPayAmount;
        }

        public void setTotalPayAmount(double totalPayAmount) {
            this.totalPayAmount = totalPayAmount;
        }

        public String getBankInfo() {
            return bankInfo;
        }

        public void setBankInfo(String bankInfo) {
            this.bankInfo = bankInfo;
        }
    }
}

class TabChangeMessage {
    private String hoaDonId;
    private float discountAmount;

    public String getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(String hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }


}
