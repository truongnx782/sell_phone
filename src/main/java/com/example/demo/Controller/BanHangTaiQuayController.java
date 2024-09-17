package com.example.demo.Controller;

import com.example.demo.Entitys.*;
import com.example.demo.Repository.*;
import com.example.demo.Request.AddProductsRequest;
import com.example.demo.Request.VoucherRequest;
import com.example.demo.Response.CartDetailResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("ban-hang-tai-quay")
public class BanHangTaiQuayController {
    @Autowired
    private ImeiDaBanRepository imeiDaBanRepository;
    @Autowired
    private ChiTietPhuongThucTTRepository chiTietPhuongThucTTRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonCTRepo;
    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private KhachHangRepo khachHangRepo;
    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired
    private ImeiRepository imeiRepository;
    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;
    @Autowired
    private KhachHangRepository khachHangRepository;
    @Autowired
    private DiaChiRepository diaChiRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @GetMapping("/index")
    public String hienThi(Model model) {
        List<HoaDon> listHoaDon = hoaDonRepository.findAllByTrangThai(0);

        if (listHoaDon.isEmpty()) {
            HoaDon newCart = new HoaDon();
            newCart.setIdNhanVien(nhanVienRepository.findById(BigInteger.valueOf(6)).get());
            newCart.setNgayTao(new Date());
            newCart.setLoaiHoaDon(1);
            newCart.setTrangThai(hoaDonRepository.HD_NEW);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
            String maHD = "HD" + dateFormat.format(new Date()) + String.format("%02d", new Date().getYear() % 100) +
                    hoaDonRepository.findAll().size();

            newCart.setMaHD(maHD);
            hoaDonRepository.save(newCart);
        }


        if (!model.containsAttribute("khachHang")) {
            model.addAttribute("khachHang", new KhachHang());
        }

        // Tính số lượng sản phẩm theo imei
        List<ChiTietSanPham> products = chiTietSanPhamRepository.findAll();
        Map<BigInteger, Integer> imeiCounts = new HashMap<>();
        for (ChiTietSanPham product : products) {
            Integer count = imeiRepository.countIMByIdCTSPAndTrangThai(product.getId());
            imeiCounts.put(product.getId(), count);
        }

        model.addAttribute("products", products);
        model.addAttribute("imeiCounts", imeiCounts);
        model.addAttribute("listHoaDon", listHoaDon);
        return "admin/ban-hang-tai-quay/index";
    }

    @GetMapping("/customer")
    public String customerPage() {
        return "admin/ban-hang-tai-quay/customer-view";
    }

    @PostMapping("/updateImeiCount")
    public ResponseEntity<String> updateImeiCount(@RequestParam("productId") BigInteger productId) {
        int newImeiCount = imeiRepository.countIMByIdCTSPAndTrangThai(productId);
        return ResponseEntity.ok(Integer.toString(newImeiCount));
    }

    @PostMapping("/search-customer")
    public ResponseEntity<KhachHang> searchCustomer(@RequestBody Map<String, String> request) {
        String search = request.get("search-customer");
        KhachHang kh = khachHangRepo.findBySdt(search);
        if (kh != null) {
            return ResponseEntity.ok(kh);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


    @PostMapping("/addCustomerToCart")
    public ResponseEntity<?> addCustomerToCart(@RequestBody Map<String, Object> payload) {
        BigInteger hoaDonId = new BigInteger(payload.get("gioHangId").toString());
        BigInteger khachHangId = new BigInteger(payload.get("khachHangId").toString());

        HoaDon hoaDon = hoaDonRepository.findById(hoaDonId).get();
        KhachHang khachHang = khachHangRepo.findById(khachHangId).get();

        hoaDon.setIdKhachHang(khachHang);
        hoaDonRepository.save(hoaDon);

        return ResponseEntity.ok("Khách hàng đã được thêm vào giỏ hàng");
    }

    // Đối tượng để lưu thông tin khách hàng cho mỗi giỏ hàng
    Map<BigInteger, KhachHang> customerInfoByCart = new HashMap<>();

    // Tìm kiếm thông tin khách hàng cho giỏ hàng được chọn
    @PostMapping("/getCustomerInfo")
    public ResponseEntity<KhachHang> getCustomerInfo(@RequestBody Map<String, BigInteger> payload) {
        BigInteger gioHangId = payload.get("gioHangId");

        KhachHang customerInfo = customerInfoByCart.get(gioHangId);

        if (customerInfo != null) {
            return ResponseEntity.ok(customerInfo);
        } else {
            HoaDon hd = hoaDonRepository.findById(gioHangId).get();

            KhachHang khachHang = hd.getIdKhachHang();
            if (khachHang != null) {
                customerInfoByCart.put(gioHangId, khachHang);
                return ResponseEntity.ok(khachHang);
            } else {
                return ResponseEntity.notFound().build();
            }
        }
    }

    @PostMapping("/getCustomerInfo1")
    public ResponseEntity<?> getCustomerInfo1(@RequestBody Map<String, Object> payload) {
        try {
            BigInteger gioHangId = new BigInteger(payload.get("gioHangId").toString());
            HoaDon hoaDon = hoaDonRepository.findById(gioHangId).get();

            if (hoaDon.getIdKhachHang() == null) {
                return ResponseEntity.badRequest().body("Hóa đơn không có khách hàng.");
            }

            KhachHang khachHang = hoaDon.getIdKhachHang();
            Optional<DiaChi> diaChiMacDinhOpt = diaChiRepository.findByIdKhachHangAndTrangThai(khachHang.getId(), 3);
            if (!diaChiMacDinhOpt.isPresent()) {
                return ResponseEntity.badRequest().body("Không tìm thấy địa chỉ mặc định của khách hàng.");
            }

            DiaChi diaChiMacDinh = diaChiMacDinhOpt.get();

            Map<String, Object> customerInfo = new HashMap<>();
            customerInfo.put("tenKhachHang", khachHang.getTenKhachHang());
            customerInfo.put("sdt", khachHang.getSdt());
            customerInfo.put("tinh", diaChiMacDinh.getTinh());
            customerInfo.put("quan", diaChiMacDinh.getQuan());
            customerInfo.put("phuong", diaChiMacDinh.getPhuong());
            customerInfo.put("diaChiCuThe", diaChiMacDinh.getDiaChiCuThe());

            return ResponseEntity.ok(customerInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đã xảy ra lỗi khi lấy thông tin khách hàng.");
        }
    }


    @GetMapping("/createCart")
    @ResponseBody
    public Map<String, Object> createCart() {
        HoaDon newCart = new HoaDon();
        newCart.setNgayTao(new Date());
        newCart.setIdNhanVien(nhanVienRepository.findById(BigInteger.valueOf(6)).get());
        newCart.setLoaiHoaDon(1);
        newCart.setTrangThai(0); // Trạng thái giỏ hàng mới
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        String maHD = "HD" + dateFormat.format(new Date()) + String.format("%02d", new Date().getYear() % 100) +
                hoaDonRepository.findAll().size();

        newCart.setMaHD(maHD);
        hoaDonRepository.save(newCart);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("newCartId", newCart.getId());
        return response;
    }

    @GetMapping("/cartCount")
    @ResponseBody
    public Map<String, Object> getCartCount() {
        long cartCount = hoaDonRepository.countByTrangThai(0);
        Map<String, Object> response = new HashMap<>();
        response.put("cartCount", cartCount);
        return response;
    }

    @GetMapping("/getImeis")
    public ResponseEntity<Page<Imei>> getImeis(@RequestParam BigInteger productId, @RequestParam int page, @RequestParam int size) {
        Pageable pageable = PageRequest.of(page, size);
        Optional<ChiTietSanPham> optionalProduct = chiTietSanPhamRepository.findById(productId);

        if (!optionalProduct.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        ChiTietSanPham chiTietSanPham = optionalProduct.get();
        // Lấy ra danh sách imei có trạng thái = 1 ( trạng thái imei active)
        Page<Imei> imeis = imeiRepository.findByIdChiTietSPAndTrangThai(chiTietSanPham, 1, pageable);
        return ResponseEntity.ok(imeis);
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

                BigDecimal giaBan = productDetail.getGiaBan();
                int imeiCount = request.getImeiList().size();
                BigDecimal imeiCountBD = BigDecimal.valueOf(imeiCount);
                BigDecimal tongTien = giaBan.multiply(imeiCountBD);
                hdct.setTongTien(tongTien);

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
            }
            sendCartUpdate(hoaDon.getId());
            return ResponseEntity.ok("Thêm thành công sản phẩm vào giỏ hàng.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Thêm thất bại sản phẩm vào giỏ hàng.");
        }
    }


    // Gửi thông tin giỏ hàng lên màn hình khách hàng
    private void sendCartUpdate(BigInteger gioHangId) {
        List<HoaDonChiTiet> cartDetails = hoaDonCTRepo.findAllByIdHoaDon(new HoaDon(gioHangId));
        List<CartDetailResponse> response = new ArrayList<>();

        for (HoaDonChiTiet cartDetail : cartDetails) {
            List<ImeiDaBan> imeiDaBanList = imeiDaBanRepository.findAllByIdHoaDonCT(cartDetail);
            List<String> imeiList = imeiDaBanList.stream().map(ImeiDaBan::getMaImei).collect(Collectors.toList());

            CartDetailResponse detailResponse = new CartDetailResponse(
                    cartDetail.getId(),
                    cartDetail.getIdChiTietSP().getIdSanPham().getTenSanPham()
                            + " "
                            + cartDetail.getIdChiTietSP().getIdRAM().getTen()
                            + " "
                            + cartDetail.getIdChiTietSP().getIdROM().getTenRom()
                            + " ("
                            + cartDetail.getIdChiTietSP().getIdMauSac().getTenMauSac()
                            + ")",
                    cartDetail.getIdChiTietSP().getGiaBan(),
                    imeiList
            );
            response.add(detailResponse);
        }

        messagingTemplate.convertAndSend("/topic/cart", response);
    }

    @GetMapping("/getCartDetails")
    public ResponseEntity<List<CartDetailResponse>> getCartDetails(@RequestParam BigInteger gioHangId) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(gioHangId).orElse(null);
            if (hoaDon != null) {
                List<HoaDonChiTiet> cartDetails = hoaDonCTRepo.findAllByIdHoaDon(hoaDon);
                List<CartDetailResponse> response = new ArrayList<>();

                for (HoaDonChiTiet cartDetail : cartDetails) {
                    // Lấy danh sách IMEI từ bảng ImeiDaBan
                    List<ImeiDaBan> imeiDaBanList = imeiDaBanRepository.findAllByIdHoaDonCT(cartDetail);
                    List<String> imeiList = imeiDaBanList.stream().map(ImeiDaBan::getMaImei).collect(Collectors.toList());

                    CartDetailResponse detailResponse = new CartDetailResponse(
                            cartDetail.getId(),
                            cartDetail.getIdChiTietSP().getIdSanPham().getTenSanPham()
                                    + " "
                                    + cartDetail.getIdChiTietSP().getIdRAM().getTen()
                                    + " "
                                    + cartDetail.getIdChiTietSP().getIdROM().getTenRom()
                                    + " ("
                                    + cartDetail.getIdChiTietSP().getIdMauSac().getTenMauSac()
                                    + ")",
                            cartDetail.getIdChiTietSP().getGiaBan(),
                            imeiList
                    );
                    response.add(detailResponse);
                }

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping("/getBestDiscountVoucher")
    public ResponseEntity<List<PhieuGiamGia>> getBestDiscountVoucher(@RequestBody VoucherRequest request) {
        BigDecimal totalAmount = request.getTotalAmount();
        BigInteger khachHangId = request.getKhachHangId();

        // Lấy danh sách phiếu giảm giá hợp lệ
        List<PhieuGiamGia> validVouchers = phieuGiamGiaRepository.findValidVouchersNew(totalAmount, khachHangId);

        if (!validVouchers.isEmpty()) {
            return ResponseEntity.ok(validVouchers);
        } else {
            return ResponseEntity.noContent().build();
        }
    }


    @PostMapping("/updateOrderVoucher/{orderId}")
    public ResponseEntity<Void> updateOrderVoucher(@PathVariable BigInteger orderId,
                                                   @RequestBody Map<String, Object> request) {
        BigInteger voucherId = request.get("voucherId") != null ? new BigInteger(request.get("voucherId").toString()) : null;

        HoaDon hoaDon = hoaDonRepository.findById(orderId).get();

        if (voucherId != null) {
            PhieuGiamGia voucher = phieuGiamGiaRepository.findById(voucherId).orElse(null);
            hoaDon.setIdPhieuGiamGia(voucher);
        } else {
            hoaDon.setIdPhieuGiamGia(null);
        }
        BigDecimal tongTien = new BigDecimal(request.get("tongTien").toString());
        BigDecimal tongTienSauGiam = new BigDecimal(request.get("tongTienSG").toString());
        hoaDon.setTongTien(tongTien);
        hoaDon.setTongTienSauGiam(tongTienSauGiam);
        hoaDonRepository.save(hoaDon);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/getVoucherById/{voucherId}")
    public ResponseEntity<PhieuGiamGia> getVoucherById(@PathVariable BigInteger voucherId) {
        Optional<PhieuGiamGia> voucher = phieuGiamGiaRepository.findById(voucherId);
        return voucher.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/deleteProductFromCart")
    public ResponseEntity<String> deleteProductFromCart(@RequestBody Map<String, Object> payload) {
        try {
            String imeiToDelete = payload.get("imei").toString();
            BigInteger gioHangId = new BigInteger(payload.get("gioHangId").toString());
            ImeiDaBan imeiDaBan = imeiDaBanRepository.findByMaImeiAndTrangThai(imeiToDelete, 1).get();
            HoaDonChiTiet cartDetail = imeiDaBan.getIdHoaDonCT();
            imeiDaBanRepository.delete(imeiDaBan);

            long remainingImeiCount = imeiDaBanRepository.countByIdHoaDonCT(cartDetail);
            if (remainingImeiCount == 0) {
                hoaDonCTRepo.delete(cartDetail);
            } else {
                BigDecimal giaBan = cartDetail.getIdChiTietSP().getGiaBan();
                BigDecimal tongTien = giaBan.multiply(BigDecimal.valueOf(remainingImeiCount));
                cartDetail.setTongTien(tongTien);
                hoaDonCTRepo.save(cartDetail);
            }

            // Update trạng thái IMEI = 1 (IMEI chưa bán)
            Imei imei = imeiRepository.findByMaImei(imeiToDelete);
            imei.setTrangThai(1);
            imeiRepository.save(imei);
            sendCartUpdate(gioHangId);
            return ResponseEntity.ok("Xóa thành công sản phẩm khỏi giỏ hàng.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Xóa thất bại sản phẩm khỏi giỏ hàng.");
        }
    }


    @PostMapping("/addInvoice")
    public ResponseEntity<?> addInvoice(@RequestBody Map<String, Object> payload) {
        try {
            BigInteger hoaDonId = new BigInteger(payload.get("gioHangId").toString());
            BigInteger idPhieuGiamGia = payload.containsKey("idPhieuGiamGia") ? new BigInteger(payload.get("idPhieuGiamGia").toString()) : null;
            boolean isBanGiaoHang = Boolean.parseBoolean(payload.get("banGiaoHang").toString());
            List<Integer> phuongThucThanhToan = (List<Integer>) payload.get("phuongThucThanhToan");
            boolean isThanhToanKhiNhanHang = Boolean.parseBoolean(payload.get("thanhToanKhiNhanHang").toString());
            BigDecimal tongTien = new BigDecimal(payload.get("tongTien").toString());
            BigDecimal tongTienSauGiam = new BigDecimal(payload.get("tongTienSG").toString());
            BigDecimal tienMat = new BigDecimal(payload.get("tienMat").toString());
            BigDecimal chuyenKhoan = new BigDecimal(payload.get("chuyenKhoan").toString());

            HoaDon hoaDon = hoaDonRepository.findById(hoaDonId).get();

            // Cập nhật trạng thái hóa đơn = 1
            hoaDon.setTongTien(tongTien);
            hoaDon.setTongTienSauGiam(tongTienSauGiam);
            hoaDonRepository.save(hoaDon);

            // Thêm idPhieuGiamGia vào hóa đơn
            if (idPhieuGiamGia != null) {
                PhieuGiamGia pgg = phieuGiamGiaRepository.findById(idPhieuGiamGia).get();
                hoaDon.setIdPhieuGiamGia(pgg);
            }

            if (isBanGiaoHang) {
                String tenNguoiNhan = payload.get("tenNguoiNhan") != null ? payload.get("tenNguoiNhan").toString() : null;
                String sdtNguoiNhan = payload.get("sdtNguoiNhan") != null ? payload.get("sdtNguoiNhan").toString() : null;
                String diaChi = payload.get("diaChi") != null ? payload.get("diaChi").toString() : null;
                String ghiChu = payload.get("ghiChu") != null ? payload.get("ghiChu").toString() : null;
                hoaDon.setTrangThai(hoaDonRepository.HD_CHO_XAC_NHAN);
                hoaDonRepository.save(hoaDon);
                if (tenNguoiNhan == null && sdtNguoiNhan == null && diaChi == null) {
                    if (hoaDon.getIdKhachHang() != null) {
                        KhachHang khachHang = hoaDon.getIdKhachHang();
                        Optional<DiaChi> diaChiMacDinhOpt = diaChiRepository.findByIdKhachHangAndTrangThai(khachHang.getId(), 1);
                        if (diaChiMacDinhOpt.isPresent()) {
                            DiaChi diaChiMacDinh = diaChiMacDinhOpt.get();
                            hoaDon.setTenNguoiNhan(khachHang.getTenKhachHang());
                            hoaDon.setSdtNguoiNhan(khachHang.getSdt());
                            hoaDon.setDiaChi(diaChiMacDinh.getDiaChiCuThe() + ", " + diaChiMacDinh.getPhuong() + ", " + diaChiMacDinh.getQuan() + ", " + diaChiMacDinh.getTinh());
                            hoaDon.setGhiChu(ghiChu);
                        } else {
                            return ResponseEntity.badRequest().body("Không tìm thấy địa chỉ mặc định của khách hàng. Vui lòng nhập thông tin người nhận.");
                        }
                    } else {
                        return ResponseEntity.badRequest().body("Vui lòng nhập đầy đủ thông tin người nhận.");
                    }
                } else {
                    hoaDon.setTenNguoiNhan(tenNguoiNhan);
                    hoaDon.setSdtNguoiNhan(sdtNguoiNhan);
                    hoaDon.setDiaChi(diaChi);
                    hoaDon.setGhiChu(ghiChu);
                }
            }
            // Insert vào bảng chi tiết phương thức thanh toán
            for (Integer hinhThuc : phuongThucThanhToan) {
                if (phuongThucThanhToan.size() > 1) {
                    ChiTietPhuongThucTT chiTietPTTT1 = new ChiTietPhuongThucTT();
                    chiTietPTTT1.setIdHoaDon(hoaDon);
                    HinhThucThanhToan hinhThucThanhToan1 = hinhThucThanhToanRepository.findByTrangThai(2);
                    chiTietPTTT1.setIdHinhThuc(hinhThucThanhToan1);
                    chiTietPTTT1.setTrangThai(1);
                    chiTietPTTT1.setTongTien(chuyenKhoan);
                    chiTietPTTT1.setGhiChu("Thanh toán thành công");
                    chiTietPhuongThucTTRepository.save(chiTietPTTT1);

                    ChiTietPhuongThucTT chiTietPTTT2 = new ChiTietPhuongThucTT();
                    chiTietPTTT2.setIdHoaDon(hoaDon);
                    HinhThucThanhToan hinhThucThanhToan2 = hinhThucThanhToanRepository.findByTrangThai(1);
                    chiTietPTTT2.setIdHinhThuc(hinhThucThanhToan2);
                    chiTietPTTT2.setTrangThai(1);
                    chiTietPTTT2.setTongTien(tongTienSauGiam.subtract(chuyenKhoan));
                    chiTietPTTT2.setGhiChu("Thanh toán thành công");
                    chiTietPhuongThucTTRepository.save(chiTietPTTT2);
                    hoaDon.setNgayThanhToan(new Date());
                    hoaDonRepository.save(hoaDon);

                    break;
                } else {
                    ChiTietPhuongThucTT chiTietPTTT = new ChiTietPhuongThucTT();
                    chiTietPTTT.setIdHoaDon(hoaDon);
                    HinhThucThanhToan hinhThucThanhToan = hinhThucThanhToanRepository.findByTrangThai(hinhThuc);
                    chiTietPTTT.setIdHinhThuc(hinhThucThanhToan);
                    chiTietPTTT.setTrangThai(1);
                    chiTietPTTT.setTongTien(tongTienSauGiam);
                    chiTietPTTT.setGhiChu("Thanh toán thành công");
                    chiTietPhuongThucTTRepository.save(chiTietPTTT);
                    hoaDon.setNgayThanhToan(new Date());
                    hoaDonRepository.save(hoaDon);
                }
            }


            if (!isBanGiaoHang) {
                LichSuHoaDon lichSuHoaDonThanhToan = new LichSuHoaDon();
                lichSuHoaDonThanhToan.setIdHoaDon(hoaDon);
                lichSuHoaDonThanhToan.setIdNhanVien(nhanVienRepository.findById(BigInteger.valueOf(6)).get());
                lichSuHoaDonThanhToan.setThoiGian(new Date());
                lichSuHoaDonThanhToan.setTrangThai(8);
                lichSuHoaDonThanhToan.setGhiChu("Đơn hàng đã hoàn thành");
                lichSuHoaDonRepository.save(lichSuHoaDonThanhToan);

                hoaDon.setNgayThanhToan(new Date());
                hoaDon.setTrangThai(hoaDonRepository.HD_HOAN_THANH);
                hoaDon.setTongTienSauGiam(tongTienSauGiam);
                hoaDon.setTongTien(tongTien);
                hoaDonRepository.save(hoaDon);

                if (hoaDon.getIdPhieuGiamGia() != null) {
                    PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(hoaDon.getIdPhieuGiamGia().getId()).get();
                    phieuGiamGia.setSoLuong(phieuGiamGia.getSoLuong() - 1);
                    phieuGiamGiaRepository.save(phieuGiamGia);
                }

            } else if (isThanhToanKhiNhanHang) {
                LichSuHoaDon lichSuHoaDonGiaoHang = new LichSuHoaDon();
                lichSuHoaDonGiaoHang.setIdHoaDon(hoaDon);
                lichSuHoaDonGiaoHang.setIdNhanVien(nhanVienRepository.findById(BigInteger.valueOf(6)).get());
                lichSuHoaDonGiaoHang.setThoiGian(new Date());
                lichSuHoaDonGiaoHang.setTrangThai(1);
                lichSuHoaDonGiaoHang.setGhiChu("Đặt hàng thành công");
                lichSuHoaDonRepository.save(lichSuHoaDonGiaoHang);
                hoaDon.setTrangThai(hoaDonRepository.HD_CHO_XAC_NHAN);
                hoaDon.setLoaiHoaDon(2);
                hoaDonRepository.save(hoaDon);
            } else {
                LichSuHoaDon lichSuHoaDonGiaoHang = new LichSuHoaDon();
                lichSuHoaDonGiaoHang.setIdHoaDon(hoaDon);
                lichSuHoaDonGiaoHang.setIdNhanVien(nhanVienRepository.findById(BigInteger.valueOf(6)).get());
                lichSuHoaDonGiaoHang.setThoiGian(new Date());
                lichSuHoaDonGiaoHang.setTrangThai(1);
                lichSuHoaDonGiaoHang.setGhiChu("Đặt hàng thành công");
                lichSuHoaDonRepository.save(lichSuHoaDonGiaoHang);
                hoaDon.setTrangThai(hoaDonRepository.HD_CHO_XAC_NHAN);
                hoaDon.setNgayThanhToan(new Date());
                hoaDon.setLoaiHoaDon(2);
                hoaDonRepository.save(hoaDon);

            }

            return ResponseEntity.ok("Thêm đơn hàng thành công");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Thêm đơn hàng thất bại");
        }
    }

    public static String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    @PostMapping("/add-customer")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addCustomer(@RequestBody @NotNull Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        String customerName = payload.get("customerName");
        String customerPhone = payload.get("customerPhone");
        String customerEmail = payload.get("customerEmail");

        if (khachHangRepository.existsBySdt(customerPhone)) {
            errors.put("customerPhone", "Số điện thoại đã tồn tại.");
        }

        if (khachHangRepository.existsByEmail(customerEmail)) {
            errors.put("customerEmail", "Email đã tồn tại.");
        }

        if (!errors.isEmpty()) {
            response.put("success", false);
            response.put("errors", errors);
            return ResponseEntity.ok(response);
        }

        try {
            String chuoi = customerName;
            String[] cacTu = chuoi.split("\\s+");
            StringBuilder ketQua = new StringBuilder();
            String truong = removeAccents(cacTu[cacTu.length - 1]);
            ketQua.append(truong);

            for (int i = 0; i < cacTu.length - 1; i++) {
                if (cacTu[i].length() > 0) {
                    ketQua.append(cacTu[i].charAt(0));
                }
            }
            // Tạo khách hàng mới
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKhachHang(ketQua.toString().toLowerCase(Locale.ROOT) + "KH" + (khachHangRepo.findAll().size() + 1));
            khachHang.setTenKhachHang(customerName);
            khachHang.setSdt(customerPhone);
            khachHang.setEmail(customerEmail);
            khachHang.setTrangThai(1);
            khachHangRepository.save(khachHang);

            // Tạo địa chỉ mới
            DiaChi diaChi = new DiaChi();
            diaChi.setDiaChiCuThe(payload.get("address"));
            diaChi.setPhuong(payload.get("ward"));
            diaChi.setQuan(payload.get("district"));
            diaChi.setTinh(payload.get("city"));
            diaChi.setIdKhachHang(khachHang.getId());
            diaChi.setTrangThai(3);
            diaChiRepository.save(diaChi);

            // Thêm khách hàng vào giỏ hàng hiện tại
            String gioHangId = payload.get("gioHangId");
            HoaDon hoaDon = hoaDonRepository.findById(new BigInteger(gioHangId)).get();
            hoaDon.setIdKhachHang(khachHang);
            hoaDonRepository.save(hoaDon);

            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }

        return ResponseEntity.ok(response);
    }

}





