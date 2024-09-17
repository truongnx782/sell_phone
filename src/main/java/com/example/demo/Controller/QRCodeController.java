package com.example.demo.Controller;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

@Controller
public class QRCodeController {

    public QRCodeController(){
        String chuoi = "Nguyễn Xuân Phúc Trường";
        String[] cacTu = chuoi.split("\\s+");
        StringBuilder ketQua = new StringBuilder();

        String truong = removeAccents(cacTu[cacTu.length - 1]);
        ketQua.append(truong);

        // Lặp qua các từ và lấy ký tự đầu tiên của mỗi từ
        for (int i = 0; i < cacTu.length - 1; i++) {
            if (cacTu[i].length() > 0) {
                ketQua.append(cacTu[i].charAt(0));
            }
        }

        System.out.println(ketQua.toString().toLowerCase());

    }
    public static String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }


    @GetMapping("/")
    public String showQRScanner() {
        return "scan/qr_scanner"; // Trả về trang HTML để hiển thị camera quét QR code
    }

    @PostMapping("/scanQRCode")
    public String scanQRCode(@RequestParam(value = "file", required = false) MultipartFile file, Model model) {
        try {
            String qrCodeText;
            if (file != null) {
                qrCodeText = decodeQRCode(file.getBytes());
            } else {
                // Không có file, thử quét từ webcam
                qrCodeText = "This is a placeholder for webcam scanning.";
            }
            System.out.println(qrCodeText);
            String[] qrCodeValues = qrCodeText.split("\\|");

            String code = qrCodeValues[0];
            String birthday = qrCodeValues[1];
            String name = qrCodeValues[2];
            String birthdate = qrCodeValues[3];
            String gender = qrCodeValues[4];
            String address = qrCodeValues[5];
            String issueDate = qrCodeValues[6];

            System.out.println("Code: " + code);
            System.out.println("Birthday: " + birthday);
            System.out.println("Name: " + name);
            System.out.println("Birthdate: " + birthdate);
            System.out.println("Gender: " + gender);
            System.out.println("Address: " + address);
            System.out.println("Issue Date: " + issueDate);


            model.addAttribute("result", qrCodeText); // Truyền kết quả cho view
            return "redirect:http://localhost:8080/khach-hang/hien-thi";
        } catch (IOException | NotFoundException e) {
            model.addAttribute("error", "QR code not found or couldn't be decoded."); // Truyền thông báo lỗi cho view
        }
        return "scan/qr_result"; // Trả về trang HTML để hiển thị kết quả
    }

    private String decodeQRCode(byte[] imageData) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, getHints());
        return qrCodeResult.getText();
    }

    private Map<DecodeHintType, Object> getHints() {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        return hints;
    }
}
