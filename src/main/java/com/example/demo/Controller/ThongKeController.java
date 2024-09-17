package com.example.demo.Controller;

import com.cloudinary.Cloudinary;
import com.example.demo.Beans.HoaDonBean;
import com.example.demo.Repository.*;
import com.example.demo.Service.EmailService;
import com.example.demo.Service.ExcelService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ThongKeController {

    @Autowired
    Cloudinary cloudinary;
    @Autowired
    KhachHangRepository khachHangRepository;
    @Autowired
    DiaChiRepository diaChiRepository;
    @Autowired
    private HangRepository hangRepository;
    @Autowired
    private CameraSauRepository cameraSauRepository;
    @Autowired
    private CameraTruocRepository cameraTruocRepository;
    @Autowired
    private ChipRepository chipRepository;
    @Autowired
    private HeDieuHanhRepository heDieuHanhRepository;
    @Autowired
    private ManHinhRepository manHinhRepository;
    @Autowired
    private PinRepository pinRepository;
    @Autowired
    private SimRepository simRepository;
    @Autowired
    private ROMRepository romRepository;
    @Autowired
    private RAMRepository ramRepository;
    @Autowired
    private MauSacRepository mauSacRepository;
    @Autowired
    private ChiTietSanPhamRepository ctspRepository;
    @Autowired
    private SanPhamRepository sanPhamRepository;
    @Autowired
    private ImeiRepository imeiRepository;
    @Autowired
    private ImeiDaBanRepository imeiDaBanRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    GioHangChiTietRepository gioHangChiTietRepository;
    @Autowired
    GioHangRepository gioHangRepository;
    @Autowired
    HoaDonRepository hoaDonRepository;
    @Autowired
    HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    PhieuGiamGiaRepository phieuGiamGiaRepository;
    @Autowired
    ChiTietPhuongThucTTRepository chiTietPhuongThucTTRepository;
    @Autowired
    HinhThucThanhToanRepository hinhThucThanhToanRepository;
    @Autowired
    LichSuHoaDonRepository lichSuHoaDonRepository;
    @Autowired
    NhanVienRepository nhanVienRepository;
    @Autowired
    private ExcelService excelService;
    @Autowired
    private EmailService emailService;

    @GetMapping("/thong-ke")
    public String index(
            Model model
    ){
        HoaDonBean hoaDonChoXacNhan = new HoaDonBean();
        HoaDonBean hoaDonChoGiao = new HoaDonBean();
        HoaDonBean hoaDonDangGiao = new HoaDonBean();
        HoaDonBean hoaDonHoanThanh = new HoaDonBean();
        HoaDonBean hoaDonHuy = new HoaDonBean();

        Integer tongSoHD = 0;
        BigDecimal tongDoanhThu = new BigDecimal(0);
        List<ThongKeInterface> lstHoaDonByTrangThai = hoaDonRepository.countHoaDonByTrangThai();
        for (ThongKeInterface tk:lstHoaDonByTrangThai) {
            if(tk.getTrangThaiHoaDon() == 1){
                hoaDonChoXacNhan.setTenTrangThai("Đơn chờ xác nhận");
                hoaDonChoXacNhan.setSoHoaDon(tk.getSoHoaDon());
                hoaDonChoXacNhan.setDoanhThu(tk.getDoanhThu());
            }else if(tk.getTrangThaiHoaDon() == 2){
                hoaDonChoGiao.setTenTrangThai("Đơn chờ giao");
                hoaDonChoGiao.setSoHoaDon(tk.getSoHoaDon());
                hoaDonChoGiao.setDoanhThu(tk.getDoanhThu());
            }else if(tk.getTrangThaiHoaDon() == 3){
                hoaDonDangGiao.setTenTrangThai("Đơn đang giao");
                hoaDonDangGiao.setSoHoaDon(tk.getSoHoaDon());
                hoaDonDangGiao.setDoanhThu(tk.getDoanhThu());
            }else if(tk.getTrangThaiHoaDon() == 4){
                hoaDonHoanThanh.setTenTrangThai("Đơn hoàn thành");
                hoaDonHoanThanh.setSoHoaDon(tk.getSoHoaDon());
                hoaDonHoanThanh.setDoanhThu(tk.getDoanhThu());
            }else if(tk.getTrangThaiHoaDon() == 5){
                hoaDonHuy.setTenTrangThai("Đơn Hủy");
                hoaDonHuy.setSoHoaDon(tk.getSoHoaDon());
                hoaDonHuy.setDoanhThu(tk.getDoanhThu());
            }
            tongSoHD = tk.getSoHoaDon()+tongSoHD;
            tongDoanhThu = tk.getDoanhThu().add(tongDoanhThu);
        }
        HoaDonBean hoaDonTong = new HoaDonBean();
        hoaDonTong.setSoHoaDon(tongSoHD);
        hoaDonTong.setDoanhThu(tongDoanhThu);

//        Báo cáo trong ngày
        List<DoanhThuTrongNgay> lstDoanhThuTrongNgay = hoaDonRepository.findDoanhThuOnToday();
        BigDecimal tongDoanhThuNgay = new BigDecimal(0);
        for (DoanhThuTrongNgay dt: lstDoanhThuTrongNgay) {
            tongDoanhThuNgay = dt.getDoanhThuNhanVien().add(tongDoanhThuNgay);
        }

//        Top 5 sản phẩm bán chạy
        List<SanPhamThongke> lstTop5SanPham = new ArrayList<>();
        if (hoaDonRepository.findTop5Products().size() > 5) {
            lstTop5SanPham =  hoaDonRepository.findTop5Products().subList(0, 5);
        } else {
            lstTop5SanPham =  hoaDonRepository.findTop5Products();
        }

        model.addAttribute("HDChoXacNhan", hoaDonChoXacNhan);
        model.addAttribute("HDChoGiao", hoaDonChoGiao);
        model.addAttribute("HDDangGiao", hoaDonDangGiao);
        model.addAttribute("HDHoanThanh", hoaDonHoanThanh);
        model.addAttribute("HDHuy", hoaDonHuy);
        model.addAttribute("HDTong", hoaDonTong);
        model.addAttribute("tongDoanhThuNgay", tongDoanhThuNgay);
        model.addAttribute("lstTop5SanPham", lstTop5SanPham);
        model.addAttribute("lstDoanhThuTrongNgay", lstDoanhThuTrongNgay);
        model.addAttribute("HoaDonTaiQuay", hoaDonRepository.getHoaDonTaiQuay());
        model.addAttribute("HoaDonOnline", hoaDonRepository.getHoaDonOnline());
        return "admin/ThongKe/thong_ke";
    }

//    Tổng doanh thu lọc theo tháng
//    Default là doanh thu theo ngày trong tháng
    @GetMapping("/doanh-thu-theo-thang")
    @ResponseBody
    public List<ThongKeInterface> getDoanhThuThang(
    ){
        return hoaDonRepository.findCurrentYearMonthlyDoanhThu();
    }

//    Hãng Lọc doanh thu theo hôm nay, 3 ngày, 1 tháng, 1 năm
    @GetMapping("/doanh-thu-theo-hang")
    @ResponseBody
    public List<ThongKeInterface> getDoanhThuHang(
    ){
        return hoaDonRepository.getDoanhThuByHang(8);
    }
//    Biểu đồ trạng thái đơn hàng

//    Top 5 sản phẩm bán chạy nhất

//    Tỷ lệ bán online và bán offline
    @GetMapping("/ty-le-phan-phoi")
    @ResponseBody
    public List<ThongKeInterface> getLoaiHoaDon(
    ){
        return hoaDonRepository.countLoaiHoaDon();
    }
//    Thống kê cuối ngày, xuất báo cáo qua excel và gửi về email
//    Tải file excel
    @GetMapping("/download/excel")
    public ResponseEntity<byte[]> downloadExcel(
    ) throws IOException
    {
        // Lấy ngày hiện tại và định dạng
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = currentDate.format(formatter);

//        Báo cáo trong ngày
        List<DoanhThuTrongNgay> lstDoanhThuTrongNgay = hoaDonRepository.findDoanhThuOnToday();
        BigDecimal tongDoanhThuNgay = new BigDecimal(0);
        Integer donOffline = hoaDonRepository.getHoaDonTaiQuay();
        Integer donOnline = hoaDonRepository.getHoaDonOnline();

        for (DoanhThuTrongNgay dt: lstDoanhThuTrongNgay) {
            tongDoanhThuNgay = dt.getDoanhThuNhanVien().add(tongDoanhThuNgay);
        }

        // Tạo dữ liệu cho Excel
        List<String[]> data = new ArrayList<>();
        data.add(new String[]{"BÁO CÁO DOANH THU NGÀY "+formattedDate});  // Tiêu đề
        data.add(new String[]{"Mã nhân viên", "Tên nhân viên", "Số đơn", "Doanh thu"});
        for (DoanhThuTrongNgay dt : lstDoanhThuTrongNgay) {
            data.add(new String[]{
                    dt.getMaNhanVien(),
                    dt.getTenNhanVien(),
                    String.valueOf(dt.getSoHoaDonTheoNhanVien()),
                    dt.getDoanhThuNhanVien().toString()
            });
        }

        // Thêm dòng hiển thị số đơn tại quầy và số đơn online
        data.add(new String[]{"", "Số đơn tại quầy", String.valueOf(donOffline)});
        data.add(new String[]{"", "Số đơn online", String.valueOf(donOnline)});

        // Thêm dòng hiển thị tổng doanh thu
        data.add(new String[]{"", "Tổng doanh thu", "", tongDoanhThuNgay.toString()});


        ByteArrayInputStream in = excelService.generateExcel(data);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=baocaodoanhthungay_" + formattedDate + ".xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(in.readAllBytes());

    }
////    Gửi qua mail
    @GetMapping("/send-email")
    public String  sendEmail(
            HttpServletResponse response,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Lấy ngày hiện tại và định dạng
            LocalDate currentDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = currentDate.format(formatter);

//        Báo cáo trong ngày
            List<DoanhThuTrongNgay> lstDoanhThuTrongNgay = hoaDonRepository.findDoanhThuOnToday();
            BigDecimal tongDoanhThuNgay = new BigDecimal(0);
            Integer donOffline = hoaDonRepository.getHoaDonTaiQuay();
            Integer donOnline = hoaDonRepository.getHoaDonOnline();

            for (DoanhThuTrongNgay dt: lstDoanhThuTrongNgay) {
                tongDoanhThuNgay = dt.getDoanhThuNhanVien().add(tongDoanhThuNgay);
            }

            // Tạo dữ liệu cho Excel
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"BÁO CÁO DOANH THU NGÀY "+formattedDate});  // Tiêu đề
            data.add(new String[]{"Mã nhân viên", "Tên nhân viên", "Số đơn", "Doanh thu"});
            for (DoanhThuTrongNgay dt : lstDoanhThuTrongNgay) {
                data.add(new String[]{
                        dt.getMaNhanVien(),
                        dt.getTenNhanVien(),
                        String.valueOf(dt.getSoHoaDonTheoNhanVien()),
                        dt.getDoanhThuNhanVien().toString()
                });
            }

            // Thêm dòng hiển thị số đơn tại quầy và số đơn online
            data.add(new String[]{"", "Số đơn tại quầy", String.valueOf(donOffline)});
            data.add(new String[]{"", "Số đơn online", String.valueOf(donOnline)});

            // Thêm dòng hiển thị tổng doanh thu
            data.add(new String[]{"", "Tổng doanh thu", "", tongDoanhThuNgay.toString()});

            // Gửi email với file Excel
            emailService.sendEmailWithExcel("oanhntk34@gmail.com", data);
            // Thêm thông báo thành công vào redirect attributes
            redirectAttributes.addFlashAttribute("successMessage", "Email sent successfully!");

            // Redirect đến URL /thong-ke sau khi gửi email thành công
            return "redirect:/thong-ke";
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error sending email: " + e.getMessage());
            return "redirect:/thong-ke";
        }
    }

}
