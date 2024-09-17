package com.example.demo.Controller;

import com.example.demo.Entitys.KhachHang;
import com.example.demo.Repository.KhachHangRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.Normalizer;
import java.util.*;

@Controller
public class LoginController {

    @Autowired
    KhachHangRepository khachHangRepository;

    @GetMapping("/client")
    public String clientLogin(){
        return "Login/login_client";
    }

    @GetMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(
            @RequestParam("email") String email,
            @RequestParam("matKhau") String matKhau,
            HttpSession session
    ) {
        Optional<KhachHang> khachHangOpt = khachHangRepository.findKhachHangsByEmail(email)
                .stream()
                .filter(kh -> kh.getMatKhau().equals(matKhau))
                .findFirst();

        if (khachHangOpt.isPresent()) {
            KhachHang khachHang = khachHangOpt.get();
            session.setAttribute("khachHang", khachHang);
            return ResponseEntity.ok(khachHang);
        } else {
            String errorMessage = !khachHangRepository.existsByEmail(email) ?
                    "Email không hợp lệ!" : "Mật khẩu không hợp lệ!";
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", errorMessage);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }


    @GetMapping("/dang-ky")
    public String clientRegister(){
        return "Login/register_client";
    }

    @PostMapping("/tao-tai-khoan")
    @ResponseBody
    public ResponseEntity<Map<String, String>> createAccount(
            @RequestParam("username") String hoTen,
            @RequestParam("sdt") String sdt,
            @RequestParam("email") String email,
            @RequestParam("password") String matKhau
    ) {
        Map<String, String> response = new HashMap<>();

        if (khachHangRepository.existsBySdt(sdt)) {
            response.put("error", "Số điện thoại đã đăng ký");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (khachHangRepository.existsByEmail(email)) {
            response.put("error", "Email đã đăng ký");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        String chuoi = hoTen;
        String[] cacTu = chuoi.split("\\s+");
        StringBuilder ketQua = new StringBuilder();
        String truong = removeAccents(cacTu[cacTu.length - 1]);
        ketQua.append(truong);

        for (int i = 0; i < cacTu.length - 1; i++) {
            if (cacTu[i].length() > 0) {
                ketQua.append(cacTu[i].charAt(0));
            }
        }

        Optional<KhachHang> maxIdKhachHang = khachHangRepository.findMaId();
        int maxId = maxIdKhachHang.isPresent() ? Integer.parseInt(String.valueOf(maxIdKhachHang.get().getId())) + 1 : 0;

        KhachHang khachHangNew = new KhachHang();
        khachHangNew.setTenKhachHang(hoTen);
        khachHangNew.setSdt(sdt);
        khachHangNew.setEmail(email);
        khachHangNew.setMatKhau(matKhau);
        khachHangNew.setTrangThai(1);
        khachHangNew.setMaKhachHang(ketQua.toString().toLowerCase(Locale.ROOT) + "KH" + maxId);
        khachHangRepository.save(khachHangNew);
        response.put("success", "Đăng ký thành công");
        return ResponseEntity.ok(response);
    }

    public static String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }


}
