package com.example.demo.Controller;

import com.example.demo.Entitys.*;
import com.example.demo.Repository.*;
import com.example.demo.Beans.StatusTextHelper;
import com.example.demo.Request.AddProductsRequest;
import com.example.demo.Request.ThongTinNguoiNhanRequest;
import com.example.demo.Service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("quan-ly-don-hang")
public class HoaDonController {
    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired
    private StatusTextHelper statusTextHelper;
    @Autowired
    private ChiTietPhuongThucTTRepository chiTietPTTTRepo;
    @Autowired
    private HinhThucThanhToanRepository htttRepository;
    @Autowired
    private ImeiDaBanRepository imeiDaBanRepository;
    @Autowired
    private EmailService sendEmail;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired
    private ImeiRepository imeiRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonCTRepo;
    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;
    @Autowired
    private ChiTietPhuongThucTTRepository chiTietPhuongThucTTRepository;

    String success;
    String error;

    @GetMapping("hien-thi")
    public String hienThi(Model model,
                          @RequestParam("tab") Optional<String> tabParam,
                          @RequestParam("page") Optional<Integer> pageParam,
                          @RequestParam(value = "search", required = false) String search) {
        int page = pageParam.orElse(0);
        String tab = tabParam.orElse("waiting-confirm-tab");

        Pageable p = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id"));

        Page<HoaDon> listHDChoXacNhan = this.hoaDonRepository.findAllByTrangThai(1, p);
        Page<HoaDon> listHDChoGiao = this.hoaDonRepository.findAllByTrangThai(2, p);
        Page<HoaDon> listHDDangGiao = this.hoaDonRepository.findAllByTrangThai(3, p);
        Page<HoaDon> listHDHoanTat = this.hoaDonRepository.findAllByTrangThai(4, p);
        Page<HoaDon> listHDDaHuy = this.hoaDonRepository.findAllByTrangThai(5, p);

        switch (tab) {
            case "waiting-confirm-tab":
                if (search != null && !search.isEmpty()) {
                    listHDChoXacNhan = this.hoaDonRepository.searchByKeyword(search, p);
                }
                model.addAttribute("listHD1", listHDChoXacNhan);
                break;
            case "waiting-ship-tab":
                if (search != null && !search.isEmpty()) {
                    listHDChoGiao = this.hoaDonRepository.searchByKeyword(search, p);
                }
                model.addAttribute("listHD2", listHDChoGiao);
                break;
            case "shipping-tab":
                if (search != null && !search.isEmpty()) {
                    listHDDangGiao = this.hoaDonRepository.searchByKeyword(search, p);
                }
                model.addAttribute("listHD3", listHDDangGiao);
                break;
            case "completed-tab":
                if (search != null && !search.isEmpty()) {
                    listHDHoanTat = this.hoaDonRepository.searchByKeyword(search, p);
                }
                model.addAttribute("listHD4", listHDHoanTat);
                break;
            case "cancelled-tab":
                if (search != null && !search.isEmpty()) {
                    listHDDaHuy = this.hoaDonRepository.searchByKeyword(search, p);
                }
                model.addAttribute("listHD5", listHDDaHuy);
                break;

        }
        model.addAttribute("listHD1", listHDChoXacNhan);
        model.addAttribute("listHD2", listHDChoGiao);
        model.addAttribute("listHD3", listHDDangGiao);
        model.addAttribute("listHD4", listHDHoanTat);
        model.addAttribute("listHD5", listHDDaHuy);

        model.addAttribute("totalPagesConfirm", listHDChoXacNhan.getTotalPages());
        model.addAttribute("totalPagesChoGiao", listHDChoGiao.getTotalPages());
        model.addAttribute("totalPagesShipping", listHDDangGiao.getTotalPages());
        model.addAttribute("totalPagesComplete", listHDHoanTat.getTotalPages());
        model.addAttribute("totalPagesCancel", listHDDaHuy.getTotalPages());

        model.addAttribute("number1", listHDChoXacNhan.getNumber());
        model.addAttribute("number2", listHDChoGiao.getNumber());
        model.addAttribute("number3", listHDDangGiao.getNumber());
        model.addAttribute("number4", listHDHoanTat.getNumber());
        model.addAttribute("number5", listHDDaHuy.getNumber());

        model.addAttribute("statusCount1", this.hoaDonRepository.findAllByTrangThai(1).size());
        model.addAttribute("statusCount2", this.hoaDonRepository.findAllByTrangThai(2).size());
        model.addAttribute("statusCount3", this.hoaDonRepository.findAllByTrangThai(3).size());
        model.addAttribute("statusCount4", this.hoaDonRepository.findAllByTrangThai(4).size());
        model.addAttribute("statusCount5", this.hoaDonRepository.findAllByTrangThai(5).size());

        model.addAttribute("currentTab", tab);

        return "admin/quan-ly-don-hang/index";
    }

    @GetMapping("/view-detail/{id}")
    public String detail(@PathVariable("id") HoaDon hd,
                         Model model,
                         HttpSession session) {

        List<ChiTietPhuongThucTT> thanhToan = chiTietPhuongThucTTRepository.findAllByIdHoaDon(hd);
        Boolean trangThaiThanhToan = false;
        boolean hienThiForm = true;
        if (thanhToan.size() == 0) {
            hienThiForm = false;
        }
        BigDecimal tongTienTrangThai1 = BigDecimal.ZERO;
        BigDecimal tongTienTrangThai2 = BigDecimal.ZERO;
        BigDecimal tongTienTrangThai3 = BigDecimal.ZERO;
        BigDecimal tongTienPhuThu = BigDecimal.ZERO;
        BigDecimal tongTienSauGiam = BigDecimal.ZERO;

        for (ChiTietPhuongThucTT tt : thanhToan) {
            Optional<BigDecimal> optionalTongTien = Optional.ofNullable(tt.getTongTien());
            if (optionalTongTien.isPresent()) {
                switch (tt.getTrangThai()) {
                    case 1:
                        tongTienTrangThai1 = tongTienTrangThai1.add(optionalTongTien.get());
                        break;
                    case 2:
                        tongTienTrangThai2 = tongTienTrangThai2.add(optionalTongTien.get());
                        break;
                    case 3:
                        tongTienTrangThai3 = tongTienTrangThai3.add(optionalTongTien.get());
                        break;
                    default:
                        break;
                }
            } else {
            }
        }


        BigDecimal tongTienThanhToan = tongTienTrangThai1.add(tongTienTrangThai3).subtract(tongTienTrangThai2);
        BigDecimal tongTienSG = hd.getTongTienSauGiam();
        BigDecimal tongTienHang = hd.getTongTien();
        BigDecimal tienGiamGia = hd.getTongTien().subtract(hd.getTongTienSauGiam());


        List<HoaDonChiTiet> hdctiet = hoaDonChiTietRepository.findAllByIdHoaDon(hd);
        BigDecimal tongTien = BigDecimal.ZERO;

        for (HoaDonChiTiet ct : hdctiet) {
            tongTien = tongTien.add(ct.getGia());
        }

        tongTienSauGiam = tongTienSauGiam.add(hd.getTongTienSauGiam());

        if (thanhToan.size() > 0) {
            trangThaiThanhToan = true;
            tongTienPhuThu = tongTienSauGiam.subtract(tongTienThanhToan);
        }
        // Định dạng số theo kiểu Việt Nam
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("#,##0", symbols);
        String formattedAmount = decimalFormat.format(tongTienThanhToan);
        String formattedTongTienSG = decimalFormat.format(tongTienSG);
        String formattedTongTienHang = decimalFormat.format(tongTienHang);
        String formattedTongTienGiamGia = decimalFormat.format(tienGiamGia);
        String formattedtongTienPhuThu = decimalFormat.format(tongTienPhuThu);
        String formattedtongTienPhuThu3 = decimalFormat.format(tongTienTrangThai3);
        String tongTienDaThanhToan = decimalFormat.format(tongTienTrangThai1);

        model.addAttribute("HienThiForm", hienThiForm);
        model.addAttribute("formattedTongTienThanhToan", formattedAmount);
        model.addAttribute("formattedTongTienSauGiam", formattedTongTienSG);
        model.addAttribute("formattedTongTienHang", formattedTongTienHang);
        model.addAttribute("formattedTongTienGiamGia", formattedTongTienGiamGia);
        model.addAttribute("tongTienPhuThu", formattedtongTienPhuThu);
        model.addAttribute("tongTienPhuThu3", formattedtongTienPhuThu3);
        model.addAttribute("tongTienPhuThuValue", tongTienPhuThu);
        model.addAttribute("tongTienPhuThu3Value", tongTienTrangThai3);
        model.addAttribute("trangThaiThanhToan", trangThaiThanhToan);
        model.addAttribute("tongTienDaThanhToan", tongTienThanhToan);


        List<LichSuHoaDon> history = lichSuHoaDonRepository.findAllByIdHoaDonOrderByThoiGianDesc(hd);
        if (history.isEmpty()) {
            LichSuHoaDon defaultRecord = new LichSuHoaDon(hd, hd.getIdNhanVien(), hd.getIdKhachHang(), new Date(), "Đặt hàng thành công", 1);
            lichSuHoaDonRepository.save(defaultRecord);
            history.add(defaultRecord);
        }
        int currentStatus = history.isEmpty() ? 0 : history.get(0).getTrangThai();
        List<LichSuHoaDon> lshd = lichSuHoaDonRepository.findAllByIdHoaDon(hd);

        // Tính số lượng sản phẩm theo imei
        List<ChiTietSanPham> products = chiTietSanPhamRepository.findAll();
        Map<BigInteger, Integer> imeiCounts = new HashMap<>();
        for (ChiTietSanPham product : products) {
            Integer count = imeiRepository.countIMByIdCTSPAndTrangThai(product.getId());
            imeiCounts.put(product.getId(), count);
        }

        model.addAttribute("products", products);
        model.addAttribute("imeiCounts", imeiCounts);

        List<ChiTietPhuongThucTT> listLSTT = this.chiTietPTTTRepo.findAllByIdHoaDon(hd);

        Boolean printBill = (Boolean) session.getAttribute("printBill");
        if (printBill != null && printBill) {
            model.addAttribute("printBill", true);
            session.removeAttribute("printBill");
        }

        Map<Integer, String> statusIcons = new HashMap<>();
        statusIcons.put(1, "bi bi-check-circle"); // Đặt hàng thành công
        statusIcons.put(2, "bi bi-clock"); // Chờ giao hàng
        statusIcons.put(3, "bi bi-truck"); // Đang giao
        statusIcons.put(4, "bi bi-house-check"); //Giao hàng thành công
        statusIcons.put(5, "bi bi-arrow-counterclockwise"); // Hoàn tác
        statusIcons.put(6, "bi bi-x-circle"); // Hủy đơn hàng
        statusIcons.put(7, "bi bi-pencil-square");// Chỉnh sửa
        statusIcons.put(8, "bi bi-check2-circle"); // Hoàn thành đơn hàng ( bán hàng tại quầy)
        HoaDon hoaDon = hoaDonRepository.findById(hd.getId()).get();
        List<HoaDonChiTiet> listHDCT = hoaDonChiTietRepository.findAllByIdHoaDon(hoaDon);
        List<ImeiDaBan> listImei = new ArrayList<>();

        for (HoaDonChiTiet hdct : listHDCT) {
            List<ImeiDaBan> imeiList = imeiDaBanRepository.findAllByIdHoaDonCT(hdct);
            listImei.addAll(imeiList);
        }

        model.addAttribute("history", history);
        model.addAttribute("currentStatus", currentStatus);
        model.addAttribute("hoaDonId", hd.getId());
        model.addAttribute("statusIcons", statusIcons);
        model.addAttribute("statusTextHelper", statusTextHelper);
        model.addAttribute("hoaDon", hd);
        model.addAttribute("listLSTT", listLSTT);
        model.addAttribute("success", success);
        model.addAttribute("lshd", lshd);
        model.addAttribute("listImei", listImei);
        model.addAttribute("trangThaiHoaDon", hd.getTrangThai());


        success = null;
        return "admin/quan-ly-don-hang/detail";
    }

    @GetMapping("/confirm-order/{idHoaDon}")
    public String confirmHoaDon(@PathVariable("idHoaDon") BigInteger id,
                                @RequestParam("note") String note,
                                HttpSession session,
                                @RequestParam("tongTien") BigDecimal tongTien,
                                @RequestParam("tongTienSG") BigDecimal tongTienSauGiam) {
        LichSuHoaDon newRecord = new LichSuHoaDon();
        Optional<HoaDon> hoaDonOptional = hoaDonRepository.findById(id);


        newRecord.setIdHoaDon(hoaDonOptional.get());
        newRecord.setGhiChu(note.isEmpty() ? "Xác nhận đơn hàng" : note);
        newRecord.setThoiGian(new Date());
        newRecord.setIdNhanVien(hoaDonOptional.get().getIdNhanVien());
        if (hoaDonOptional.get().getIdKhachHang() != null) {
            newRecord.setIdKhachHang(hoaDonOptional.get().getIdKhachHang());
        }
        newRecord.setTrangThai(lichSuHoaDonRepository.CONFIRM);

        lichSuHoaDonRepository.save(newRecord);

        HoaDon hoaDon = hoaDonOptional.get();
        hoaDon.setTongTien(tongTien);
        hoaDon.setTrangThai(hoaDonRepository.HD_CHO_GIAO_HANG);
        hoaDon.setTongTienSauGiam(tongTienSauGiam);
        hoaDonRepository.save(hoaDon);

        session.setAttribute("printBill", true);
        success = "Cập nhật đơn hàng thành công!";
        return "redirect:/quan-ly-don-hang/view-detail/" + id;
    }

    @PostMapping("/cancel-order/{idHoaDon}")
    public String cancelHoaDon(@PathVariable("idHoaDon") BigInteger id,
                               @RequestParam("description") String description) {
        HoaDon hoaDon = hoaDonRepository.findById(id).get();
        LichSuHoaDon newRecord = new LichSuHoaDon();
        newRecord.setIdHoaDon(hoaDonRepository.findById(id).get());
        newRecord.setGhiChu(description);
        newRecord.setThoiGian(new Date());
        if (hoaDon.getIdKhachHang() != null) {
            newRecord.setIdKhachHang(hoaDon.getIdKhachHang());
        }
        newRecord.setIdNhanVien(hoaDonRepository.findById(id).get().getIdNhanVien());
        newRecord.setTrangThai(lichSuHoaDonRepository.CANCEL);
        lichSuHoaDonRepository.save(newRecord);

        List<ImeiDaBan> listIMDaBan = imeiDaBanRepository.findAllByIdHoaDonCT(hoaDonCTRepo.findByIdHoaDon(hoaDon));
        listIMDaBan.forEach(im -> {
            im.setTrangThai(2);
            Imei imei = imeiRepository.findByMaImei(im.getMaImei());
            imei.setTrangThai(1);
            imeiDaBanRepository.save(im);
            imeiRepository.save(imei);
        });

        hoaDon.setTrangThai(hoaDonRepository.HD_DA_HUY);
        hoaDon.setGhiChu(description);
        hoaDon.setNgayHuy(new Date());
        hoaDonRepository.save(hoaDon);
        success = "Cập nhật đơn hàng thành công!";
        return "redirect:/quan-ly-don-hang/view-detail/" + id;
    }


    @GetMapping("/ship-order/{idHoaDon}")
    public String shipHoaDon(
            @PathVariable("idHoaDon") BigInteger id,
            @RequestParam("note") String note,
            @RequestParam("totalAmount") BigDecimal totalAmount,
            @RequestParam("tongTienSauGiam") BigDecimal tongTienSauGiam) {

        HoaDon hoaDon = hoaDonRepository.findById(id).get();
        hoaDon.setTongTien(totalAmount);
        hoaDon.setTrangThai(hoaDonRepository.HD_DANG_GIAO);
        hoaDon.setTongTienSauGiam(tongTienSauGiam);
        hoaDonRepository.save(hoaDon);

        if (hoaDon.getIdPhieuGiamGia() != null && hoaDon.getIdPhieuGiamGia().getTrangThaiSoLuong() == 0) {
            PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(hoaDon.getIdPhieuGiamGia().getId()).get();
            phieuGiamGia.setSoLuong(phieuGiamGia.getSoLuong() - 1);
            phieuGiamGiaRepository.save(phieuGiamGia);
        }

        LichSuHoaDon newRecord = new LichSuHoaDon();
        newRecord.setIdHoaDon(hoaDon);
        if (hoaDon.getIdKhachHang() != null) {
            newRecord.setIdKhachHang(hoaDon.getIdKhachHang());
        }
        newRecord.setGhiChu(note.isEmpty() ? "Xác nhận giao hàng cho đơn vị vận chuyển" : note);
        newRecord.setThoiGian(new Date());
        newRecord.setIdNhanVien(hoaDon.getIdNhanVien());
        newRecord.setTrangThai(LichSuHoaDonRepository.SHIP);
        lichSuHoaDonRepository.save(newRecord);

        success = "Cập nhật đơn hàng thành công!";
        return "redirect:/quan-ly-don-hang/view-detail/" + id;
    }

    @GetMapping("/complete-order/{idHoaDon}")
    public String completeHoaDon(@PathVariable("idHoaDon") BigInteger id,
                                 @RequestParam("note") String note,
                                 @RequestParam("totalAmount") BigDecimal totalAmount,
                                 @RequestParam("tongTienSauGiam") BigDecimal totalPayAmount) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(id).get();

            LichSuHoaDon newRecord = new LichSuHoaDon();
            newRecord.setIdHoaDon(hoaDon);
            newRecord.setGhiChu(note.isEmpty() ? "Giao hàng thành công" : note);
            newRecord.setThoiGian(new Date());
            if (hoaDon.getIdKhachHang() != null) {
                newRecord.setIdKhachHang(hoaDon.getIdKhachHang());
            }
            newRecord.setIdNhanVien(hoaDon.getIdNhanVien());
            newRecord.setTrangThai(lichSuHoaDonRepository.COMPLETE);
            lichSuHoaDonRepository.save(newRecord);

            // Cập nhật tổng tiền và tổng tiền sau giảm
            hoaDon.setTongTien(totalAmount);
            hoaDon.setTongTienSauGiam(totalPayAmount);
            hoaDon.setNgayThanhToan(new Date());
            hoaDon.setNgayNhanHang(new Date());
            hoaDon.setTrangThai(hoaDonRepository.HD_HOAN_THANH);
            hoaDonRepository.save(hoaDon);

            if (chiTietPTTTRepo.findAllByIdHoaDon(hoaDon).isEmpty()) {
                ChiTietPhuongThucTT thanhToan = new ChiTietPhuongThucTT();
                thanhToan.setIdHoaDon(hoaDon);
                thanhToan.setTongTien(totalPayAmount);
                thanhToan.setGhiChu("Thanh toán thành công");
                BigInteger idHT = BigInteger.valueOf(4);
                thanhToan.setIdHinhThuc(htttRepository.findById(idHT).get());
                thanhToan.setTrangThai(1);
                chiTietPTTTRepo.save(thanhToan);
            }

            // Lấy thông tin khách hàng
            String emailKhachHang = hoaDon.getIdKhachHang().getEmail();
            String tenKH = hoaDon.getIdKhachHang().getTenKhachHang();
            String maHD = hoaDon.getMaHD();
            String ngayNhanHang = new SimpleDateFormat("dd/MM/yyyy").format(hoaDon.getNgayNhanHang());

            // Gửi email
            sendOrderCompletionEmail(emailKhachHang, tenKH, maHD, ngayNhanHang);

            success = "Cập nhật đơn hàng thành công!";
            return "redirect:/quan-ly-don-hang/view-detail/" + id;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/quan-ly-don-hang/view-detail/" + id;
        }
    }

    @Autowired
    private JavaMailSender javaMailSender;

    private void sendOrderCompletionEmail(String to, String customerName, String orderNumber, String ngayNhanHang) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Đơn hàng của bạn đã hoàn thành!");
            message.setText("Xin chào " + customerName + ",\n\n" +
                    "Đơn hàng #" + orderNumber + " của bạn đã được giao thành công ngày " + ngayNhanHang + ".\n\n" +
                    "Chúng tôi hi vọng bạn hài lòng với sản phẩm của mình. " +
                    "Nếu có bất kỳ phản hồi hoặc câu hỏi nào, vui lòng liên hệ với chúng tôi " +
                    "qua email nguyenxuantruongtest1@gmail.com " +
                    "hoặc số điện thoại 0123654789 để được hỗ trợ kịp thời.\n\n" +
                    "Cảm ơn bạn đã mua hàng tại MPSHOP. Hy vọng được phục vụ bạn lần tới!\n\n" +
                    "Trân trọng,\nMPSHOP");
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @PostMapping("/undo-order/{idHoaDon}")
    public String undoHoaDon(@PathVariable("idHoaDon") BigInteger id,
                             @RequestParam("ghiChu") String ghiChu) {
        HoaDon hoaDon = hoaDonRepository.findById(id).get();
        hoaDon.setTrangThai(hoaDon.getTrangThai() - 1);
        hoaDonRepository.save(hoaDon);
        LichSuHoaDon hoantac = new LichSuHoaDon();
        hoantac.setIdHoaDon(hoaDonRepository.findById(id).get());
        hoantac.setGhiChu(ghiChu);
        hoantac.setThoiGian(new Date());
        hoantac.setIdNhanVien(hoaDonRepository.findById(id).get().getIdNhanVien());
        hoantac.setTrangThai(lichSuHoaDonRepository.UNDO);
        this.lichSuHoaDonRepository.save(hoantac);

        success = "Cập nhật đơn hàng thành công!";
        return "redirect:/quan-ly-don-hang/view-detail/" + id;
    }


    @PostMapping("/addProductsToCart")
    public ResponseEntity<String> addProductsToCart(@RequestBody AddProductsRequest request) {
        try {
            // Quét mã QR
            List<ChiTietSanPham> productDetailsScannerQR = new ArrayList<>();
            for (String imeiStr : request.getImeiList()) {
                Imei imei = imeiRepository.findByMaImei(imeiStr);
                if (imei == null || imei.getTrangThai() != 1) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Imei vừa quét đã được bán hoặc không tồn tại.");
                }
                productDetailsScannerQR.add(imei.getIdChiTietSP());
            }


            List<ChiTietSanPham> productDetails = chiTietSanPhamRepository.findByImeiList(request.getImeiList());
            HoaDon hoaDon = hoaDonRepository.findById(request.getGioHangId()).get();
            BigDecimal tongTien = new BigDecimal(request.getTongTien().toString());
            BigDecimal tongTienSauGiam = new BigDecimal(request.getTongTienSG().toString());
            hoaDon.setTongTien(tongTien);
            hoaDon.setTongTienSauGiam(tongTienSauGiam);
            hoaDonRepository.save(hoaDon);
            for (ChiTietSanPham productDetail : productDetails) {
                Optional<HoaDonChiTiet> existingHdctOpt = hoaDonCTRepo.findByIdHoaDonAndIdChiTietSP(hoaDon, productDetail);

                HoaDonChiTiet hdct;
                if (existingHdctOpt.isPresent()) {
                    hdct = existingHdctOpt.get();
                } else {
                    hdct = new HoaDonChiTiet();
                    hdct.setIdChiTietSP(productDetail);
                    hdct.setIdHoaDon(hoaDon);
                    hdct.setGia(productDetail.getGiaBan());
                    hdct.setTrangThai(1);
                }

                hoaDonCTRepo.save(hdct);

                // Thêm các IMEI vào bảng ImeiDaBan
                for (String imeiStr : request.getImeiList()) {
                    ImeiDaBan imeiDaBan = new ImeiDaBan();
                    imeiDaBan.setIdHoaDonCT(hdct);
                    imeiDaBan.setMaImei(imeiStr);
                    imeiDaBan.setTrangThai(1);
                    imeiDaBanRepository.save(imeiDaBan);

                    // Update trạng thái của IMEI
                    Imei imei = imeiRepository.findByMaImei(imeiStr);
                    imei.setTrangThai(2);
                    imeiRepository.save(imei);
                }

                // Set idPhieuGiamGia trong hóa đơn thành null
                HoaDon hd = hoaDonRepository.findById(request.getGioHangId()).get();
                hd.setIdPhieuGiamGia(null);
                hoaDonRepository.save(hd);


                LichSuHoaDon newRecord = new LichSuHoaDon();
                newRecord.setIdHoaDon(hoaDonRepository.findById(hoaDon.getId()).get());
                newRecord.setGhiChu("Thêm sản phẩm: "
                        + hdct.getIdChiTietSP().getIdSanPham().getTenSanPham() + " "
                        + hdct.getIdChiTietSP().getIdRAM().getTen() + " "
                        + hdct.getIdChiTietSP().getIdROM().getTenRom() + " ("
                        + hdct.getIdChiTietSP().getIdMauSac().getTenMauSac() + " )");
                newRecord.setThoiGian(new Date());
                if (hoaDon.getIdKhachHang() != null) {
                    newRecord.setIdKhachHang(hoaDon.getIdKhachHang());
                }
                newRecord.setIdNhanVien(hoaDonRepository.findById(hoaDon.getId()).get().getIdNhanVien());
                newRecord.setTrangThai(lichSuHoaDonRepository.EDIT);

                lichSuHoaDonRepository.save(newRecord);
            }

            return ResponseEntity.ok("Thêm thành công sản phẩm vào giỏ hàng.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Thêm thất bại sản phẩm vào giỏ hàng.");
        }
    }


    @PostMapping("/deleteProductFromCart")
    public ResponseEntity<String> deleteProductFromCart(@RequestBody Map<String, Object> payload) {
        try {
            String imeiToDelete = payload.get("imei").toString();
            BigInteger hoaDonId = new BigInteger(payload.get("gioHangId").toString());
            HoaDon hoaDon = hoaDonRepository.findById(hoaDonId).get();
            BigDecimal tongTien = new BigDecimal(payload.get("tongTien").toString());
            BigDecimal tongTienSauGiam = new BigDecimal(payload.get("tongTienSG").toString());
            hoaDon.setTongTien(tongTien);
            hoaDon.setTongTienSauGiam(tongTienSauGiam);
            hoaDonRepository.save(hoaDon);
            ImeiDaBan imeiDaBan = imeiDaBanRepository.findByMaImeiAndTrangThai(imeiToDelete, 1).get();
            HoaDonChiTiet cartDetail = imeiDaBan.getIdHoaDonCT();


            imeiDaBanRepository.delete(imeiDaBan);

            long remainingImeiCount = imeiDaBanRepository.countByIdHoaDonCT(cartDetail);
            if (remainingImeiCount == 0) {
                hoaDonCTRepo.delete(cartDetail);
            }

            // Update trạng thái của IMEI => 1
            Imei imei = imeiRepository.findByMaImei(imeiToDelete);
            imei.setTrangThai(1);
            imeiRepository.save(imei);

            // Set idPhieuGiamGia trong hóa đơn thành null
            HoaDon hd = hoaDonRepository.findById(hoaDonId).get();
            hd.setIdPhieuGiamGia(null);
            hoaDonRepository.save(hd);

            LichSuHoaDon newRecord = new LichSuHoaDon();
            newRecord.setIdHoaDon(hoaDonRepository.findById(hoaDonId).get());
            newRecord.setGhiChu("Xóa sản phẩm: "
                    + cartDetail.getIdChiTietSP().getIdSanPham().getTenSanPham() + " "
                    + cartDetail.getIdChiTietSP().getIdRAM().getTen() + " "
                    + cartDetail.getIdChiTietSP().getIdROM().getTenRom() + " ("
                    + cartDetail.getIdChiTietSP().getIdMauSac().getTenMauSac() + " )");
            newRecord.setThoiGian(new Date());
            if (hoaDon.getIdKhachHang() != null) {
                newRecord.setIdKhachHang(hoaDon.getIdKhachHang());
            }
            newRecord.setIdNhanVien(hoaDonRepository.findById(hoaDonId).get().getIdNhanVien());
            newRecord.setTrangThai(lichSuHoaDonRepository.EDIT);
            lichSuHoaDonRepository.save(newRecord);

            return ResponseEntity.ok("Xóa thành công.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa thất bại sản phẩm khỏi giỏ hàng.");
        }
    }


    @PostMapping("/payment-details")
    public ResponseEntity<?> addPaymentDetails(@RequestBody ChiTietPhuongThucTT paymentDetail) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(paymentDetail.getIdHoaDon().getId()).get();
            paymentDetail.setIdHoaDon(hoaDon);
            chiTietPhuongThucTTRepository.save(paymentDetail);
            return ResponseEntity.ok("Thanh toán thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống");
        }
    }

    @PostMapping("/update-thong-tin-nguoi-nhan")
    public ResponseEntity<Map<String, Object>> updateCustomerInfo(@RequestBody ThongTinNguoiNhanRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            HoaDon hoaDon = hoaDonRepository.findById(request.getHoaDonId()).get();
            hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
            hoaDon.setSdtNguoiNhan(request.getSdtNguoiNhan());
            hoaDon.setDiaChi(request.getDiaChi());

            hoaDonRepository.save(hoaDon);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
