package com.example.demo.Controller;

import com.cloudinary.Cloudinary;
import com.example.demo.Beans.Bank;
import com.example.demo.Entitys.*;
import com.example.demo.Repository.*;
import com.example.demo.Request.ClientGioHangRequest;
import com.example.demo.Request.DiaChiRequest;
import com.example.demo.Service.VnPayService;
import com.example.demo.Utils.RepoUtuils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;

//import javax.validation.Valid;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ClientController extends RepoUtuils {

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


    private final VnPayService vnpayService;

    @Autowired
    public ClientController(VnPayService vnpayService) {
        this.vnpayService = vnpayService;
    }


    List<SanPhamHome> lstSanPhamHome = new ArrayList<>();
    GioHang gioHangGlobal = new GioHang();
    GioHangChiTiet gioHangChiTietGlobal = new GioHangChiTiet();
    List<Imei> lstImei_Global;
    BigDecimal _tongHoaDon;
    HoaDon _hoaDon;



    @GetMapping("/trang-chu")
    public String home(
            @RequestParam("page") Optional<Integer> pageParam,
            HttpSession session,
            Model model
    ){
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if(khachHang != null){
            Optional<KhachHang> khachHangOpt = khachHangRepository.findKhachHangsByEmail(khachHang.getEmail())
                    .stream()
                    .filter(kh -> kh.getMatKhau().equals(khachHang.getMatKhau()))
                    .findFirst();
            model.addAttribute("KhachHang", khachHangOpt.get());
            GioHang gioHangNew = gioHangRepository.getGioHangByIdKhachHang(khachHangOpt.get().getId());
            if(gioHangRepository.getGioHangByIdKhachHang(khachHangOpt.get().getId()) == null){
                gioHangNew = new GioHang();
                gioHangNew.setTrangThai(1);
                gioHangRepository.save(gioHangNew);
                session.setAttribute("gioHang", gioHangNew);
                model.addAttribute("lstImage", null);
                model.addAttribute("gioHangChiTiet", null);
                System.out.println("Chưa có giỏ hàng");
            }else {
                List<GioHangChiTiet> lstGHCT = gioHangChiTietRepository.getGioHangChiTietsByIdGioHang(gioHangNew.getId());
                List<Image> lstImage = imageRepository.findByTrangThai(2);
                Set<BigInteger> uniqueSanPhamIDs = new HashSet<>();
                List<Image> filteredImages = new ArrayList<>();
                for (Image image : lstImage) {
                    if (uniqueSanPhamIDs.add(image.getSanPhamId().getId())) {
                        filteredImages.add(image);
                    }
                }
                session.setAttribute("gioHang", gioHangNew);
                model.addAttribute("lstImage", filteredImages);
                model.addAttribute("gioHangChiTiet", lstGHCT);
            }
        }else {
            model.addAttribute("KhachHang", null);
        }

        model.addAttribute("lstHang", hangRepository.findByTrangThai(HangRepository.ACTIVE));
        model.addAttribute("lstRom", romRepository.findByTrangThai(ROMRepository.ACTIVE));
        model.addAttribute("lstRam", ramRepository.findByTrangThai(RAMRepository.ACTIVE));
        model.addAttribute("lstHeDieuHanh", heDieuHanhRepository.findByTrangThai(HeDieuHanhRepository.ACTIVE));
        model.addAttribute("lstManHinh", manHinhRepository.findByTrangThai(ManHinhRepository.ACTIVE));
        model.addAttribute("lstChip", chipRepository.findByTrangThai(ChipRepository.ACTIVE));
        model.addAttribute("lstPin", pinRepository.findByTrangThai(PinRepository.ACTIVE));
        model.addAttribute("lstSim", simRepository.findByTrangThai(SimRepository.ACTIVE));
        model.addAttribute("lstSanPham", sanPhamRepository.findByTrangThai(SanPhamRepository.ACTIVE));
        model.addAttribute("lstImages", imageRepository.findByTrangThai(imageRepository.ACTIVE));

        model.addAttribute("data", this.getPaginatedSPHome(0,5));
        model.addAttribute("dataAll", this.getListSPHome());

        return "client/index";
    }

    public List<SanPhamHome> getListSPHome(){
       List<SanPham> lstSanPham = sanPhamRepository.findByTrangThai(SanPhamRepository.ACTIVE);
       List<ChiTietSanPham> lstCTSP = ctspRepository.findByTrangThai(SanPhamRepository.ACTIVE);
       List<ManHinh> lstManHinh = manHinhRepository.findByTrangThai(SanPhamRepository.ACTIVE);
       List<Image> lstImage = imageRepository.findByTrangThai(SanPhamRepository.ACTIVE);
       List<ROM> lstRom = romRepository.findByTrangThai(SanPhamRepository.ACTIVE);

        List<SanPhamHome> lstSanPhamHome = new ArrayList<>();

        for (ChiTietSanPham ctsp: lstCTSP) {
            SanPhamHome sph = new SanPhamHome();
            sph.setIdChiTietSP(ctsp.getId());
            sph.setIdSanPham(ctsp.getIdSanPham().getId());
            sph.setTenSanPham(ctsp.getIdSanPham().getTenSanPham()+" "+ ctsp.getIdROM().getTenRom()+"/"+ ctsp.getIdRAM().getTen());
            sph.setGiaBan(ctsp.getGiaBan());
            lstSanPhamHome.add(sph);
        }

        for (SanPhamHome sph: lstSanPhamHome) {
            for (SanPham sp: lstSanPham ) {
                if(sph.getIdSanPham() == sp.getId()){
                    sph.setIdManHinh(sp.getIdManHinh().getId());
                    sph.setTenManHinh(sp.getIdManHinh().getTen());
                }
            }
        }

        for (SanPhamHome sph: lstSanPhamHome) {
            List<BigInteger> listIDAnh = new ArrayList<>();
            List<String> listTenAnh = new ArrayList<>();
            for (Image image: lstImage ) {
                if(sph.getIdSanPham() == image.getSanPhamId().getId()){
                    listIDAnh.add(image.getId());
                    listTenAnh.add(image.getTenAnh());
                }
                sph.setIdImage(listIDAnh);
                sph.setTenAnh(listTenAnh);
            }
        }

        List<SanPhamHome> lstSPHome = new ArrayList<>();
        int count = 0;
        for (SanPhamHome sph: lstSanPhamHome) {
            for (SanPham sp: lstSanPham ) {
                if(sph.getIdSanPham() == sp.getId()){
                    if(count == 0){
                        sph.setIdManHinh(sp.getIdManHinh().getId());
                        sph.setTenManHinh(sp.getIdManHinh().getTen());
                        count++;
                    }

                }
            }
        }

        Set<BigInteger> seenProductIds = new HashSet<>();
        lstSPHome = lstSanPhamHome.stream()
                .filter(pd -> seenProductIds.add(pd.getIdSanPham()))
                .sorted((pd1, pd2) -> pd2.getIdSanPham().compareTo(pd1.getIdSanPham()))
                .limit(10)
                .collect(Collectors.toList());

        lstSanPhamHome = lstSanPhamHome.stream()
                .filter(pd -> seenProductIds.add(pd.getIdSanPham()))
                .sorted((pd1, pd2) -> pd2.getIdSanPham().compareTo(pd1.getIdSanPham()))
                .collect(Collectors.toList());

        for (SanPhamHome sp:lstSPHome ) {
            List<BigInteger> lstIDBoNhoTrong = new ArrayList<>();
            List<String> lstTenBoNhoTrong = new ArrayList<>();
            for (ChiTietSanPham ctsp:lstCTSP) {
                if(sp.getIdSanPham() == ctsp.getIdSanPham().getId()){
                    lstIDBoNhoTrong.add(ctsp.getIdROM().getId());
                    lstTenBoNhoTrong.add(ctsp.getIdROM().getTenRom());
                }
            }
            Set<String> uniqueTenRom= new HashSet<>(lstTenBoNhoTrong);
            Set<BigInteger> uniqueIDRom = new HashSet<>(lstIDBoNhoTrong);
            List<BigInteger> uniqueListIDRom = new ArrayList<>(uniqueIDRom);
            List<String> uniqueListTenRom = new ArrayList<>(uniqueTenRom);
            sp.setIdROM(uniqueListIDRom);
            sp.setTenRom(uniqueListTenRom);
        }
        return lstSPHome;
    }

    public Page<SanPhamHome> getPaginatedSPHome(int page, int size) {
        List<SanPhamHome> sanPhamHomes = this.getListSPHome();
        Pageable pageable = PageRequest.of(page, size);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), sanPhamHomes.size());

        List<SanPhamHome> pagedList = sanPhamHomes.subList(start, end);

        return new PageImpl<>(pagedList, pageable, sanPhamHomes.size());
    }

    public SanPhamHome getSanPhamHome(BigInteger id){
        SanPhamHome sanPhamHome = new SanPhamHome();
        for (SanPhamHome sp: this.getListSPHome()) {
            if(sp.getIdSanPham().equals(id)){
                sanPhamHome = sp;
            }
        }
        return sanPhamHome;
    }


    @GetMapping("/chi-tiet-san-pham/{idSanPham}")
    public String getProductDetail(
            @PathVariable BigInteger idSanPham,
            HttpSession session,
            Model model
    ){
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if(khachHang != null){
            Optional<KhachHang> khachHangOpt = khachHangRepository.findKhachHangsByEmail(khachHang.getEmail())
                    .stream()
                    .filter(kh -> kh.getMatKhau().equals(khachHang.getMatKhau()))
                    .findFirst();
            model.addAttribute("KhachHang", khachHangOpt.get());
        }else {
            model.addAttribute("KhachHang", null);
        }
        List<ChiTietSanPham> lstCTSP = ctspRepository.findByTrangThai(SanPhamRepository.ACTIVE);
        List<ROM> lstRom = romRepository.findByTrangThai(SanPhamRepository.ACTIVE);
        SanPhamHome sph = this.getSanPhamHome(idSanPham);
        List<List<String>> lstTenAnh = new ArrayList<>();
        int chunkSize = 8;
        for (int i = 0; i < sph.getTenAnh().size(); i += chunkSize) {
            int end = Math.min(sph.getTenAnh().size(), i + chunkSize);
            lstTenAnh.add(sph.getTenAnh().subList(i, end));
        }

        List<RomChiTietSanPham> lstRomClient = new ArrayList<>();

//        Thêm list rom trong màn chi tiết sản phẩm
        for (BigInteger idRom: sph.getIdROM()) {
            RomChiTietSanPham rom = new RomChiTietSanPham();
            rom.setIdRom(idRom);
            rom.setIdSanPham(sph.getIdSanPham());
            lstRomClient.add(rom);
        }
        for (RomChiTietSanPham r:lstRomClient) {
            for (ROM rom:lstRom) {
                if(r.getIdRom() == rom.getId()){
                    r.setTenRom(rom.getTenRom());
                }
            }
        }
        System.out.println("List rom 1 "+lstRomClient.size());
        for (RomChiTietSanPham r:lstRomClient) {
            List<BigInteger> lstIDMauSac = new ArrayList<>();
            List<String> lstMaMauSac = new ArrayList<>();
            List<String> lstTenMauSac = new ArrayList<>();
            for (ChiTietSanPham ctsp: lstCTSP) {
                if(r.getIdSanPham() == ctsp.getIdSanPham().getId() && r.getIdRom() == ctsp.getIdROM().getId()){
                    lstIDMauSac.add(ctsp.getIdMauSac().getId());
                    lstTenMauSac.add(ctsp.getIdMauSac().getTenMauSac());
                    lstMaMauSac.add(ctsp.getIdMauSac().getMaMauSac());
                    r.setGiaBan(ctsp.getGiaBan());
                }
            }
            r.setTenMauSac(lstTenMauSac);
            r.setIdMauSac(lstIDMauSac);
            r.setMaMauSac(lstMaMauSac);
        }
        //Thêm ảnh cho từng màu sắc
        for (RomChiTietSanPham r:lstRomClient) {
            for (BigInteger m: r.getIdMauSac()) {
                List<String> lstAnh = imageRepository.findBySanPhamIdAndMauSacId(r.getIdSanPham(),m);
                r.setTenAnh(lstAnh);
            }
        }

        SanPham sanPham = sanPhamRepository.getReferenceById(idSanPham);

        model.addAttribute("lstTenAnh", lstTenAnh);
        model.addAttribute("sanPham", sph);
        model.addAttribute("sanPhamInfo", sanPham);
        model.addAttribute("lstRom", lstRomClient);
        for (RomChiTietSanPham r:lstRomClient) {
            System.out.println("Giá sp trong ctsp "+r.getGiaBan());
        }
        model.addAttribute("data", this.getPaginatedSPHome(0,5));
        return "client/chi_tiet_san_pham";
    }

    @PostMapping("/add-to-cart")
    @ResponseBody
    public ResponseEntity<String> addToCart(
            @RequestBody ClientGioHangRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model
    ){
        try {
            ChiTietSanPham chiTietSanPham = ctspRepository.getChiTietSanPhamByIdSanPhamAndIdROMAndIdMauSac(request.getProductId(),request.getRomId(),request.getColorId());
//          Lấy ngày hiện tại
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String ngayHienTai = currentDateTime.format(formatter);
            Date ngayTao = new Date();
            try {
                ngayTao = dateFormat.parse(ngayHienTai);
            } catch (Exception e) {
                e.printStackTrace();
            }

            GioHang gioHang = (GioHang) session.getAttribute("gioHang");
            KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
            GioHang gioHangNew = new GioHang();
            if(khachHang == null){  //      đăng nhập
//            Thông tin khách hàng null
                model.addAttribute("KhachHang", null);
                gioHang = (GioHang) session.getAttribute("gioHang");
                if(gioHang == null){               //            Kiểm tra secction, giỏ hàng null
                    gioHangNew.setTrangThai(1);
                    gioHangRepository.save(gioHangNew);
                    session.setAttribute("gioHang", gioHangNew);
                    model.addAttribute("lstImage", null);
                    model.addAttribute("gioHangChiTiet", null);
                }else {              //            Kiểm tra section, giỏ hàng khác null
                    gioHangNew = gioHang;
                    List<GioHangChiTiet> lstGHCT = gioHangChiTietRepository.getGioHangChiTietsByIdGioHang(gioHangNew.getId());
                    if(lstGHCT.size() > 0){    //Giỏ hàng chi tiết đã có sản phẩm
                        List<Image> lstImage = imageRepository.findByTrangThai(2);
                        Set<BigInteger> uniqueSanPhamIDs = new HashSet<>();
                        List<Image> filteredImages = new ArrayList<>();
                        for (Image image : lstImage) {
                            if (uniqueSanPhamIDs.add(image.getSanPhamId().getId())) {
                                filteredImages.add(image);
                            }
                        }
                        model.addAttribute("lstImage", filteredImages);
                        model.addAttribute("gioHangChiTiet", lstGHCT);
                    }else {     //Giỏ hàng chi tiết chưa có sản phẩm
                        model.addAttribute("lstImage", null);
                        model.addAttribute("gioHangChiTiet", null);
                    }
                }
            }else {    //     Không đăng nhập

                //            Lấy thông tin khách hàng khi đăng nhập thành công
                Optional<KhachHang> khachHangOpt = khachHangRepository.findKhachHangsByEmail(khachHang.getEmail())
                        .stream()
                        .filter(kh -> kh.getMatKhau().equals(khachHang.getMatKhau()))
                        .findFirst();
                model.addAttribute("KhachHang", khachHangOpt.get());
                gioHangNew = gioHangRepository.getGioHangByIdKhachHang(khachHang.getId());
//            Kiểm tra giỏ hàng
                if(gioHangRepository.getGioHangByIdKhachHang(khachHang.getId()) == null){ //Nếu chưa có giỏ hàng
                    gioHangNew = new GioHang();
                    gioHangNew.setTrangThai(1);
                    gioHangRepository.save(gioHangNew);
                    model.addAttribute("lstImage", null);
                    model.addAttribute("gioHangChiTiet", null);
                    System.out.println("Chưa có giỏ hàng");
                }else {                                                             // Đã có giỏ hàng

                    List<GioHangChiTiet> lstGHCT = gioHangChiTietRepository.getGioHangChiTietsByIdGioHang(gioHangNew.getId());
                    if(lstGHCT.size() > 0){    //Giỏ hàng chi tiết đã có sản phẩm
                        List<Image> lstImage = imageRepository.findByTrangThai(2);
                        Set<BigInteger> uniqueSanPhamIDs = new HashSet<>();
                        List<Image> filteredImages = new ArrayList<>();
                        for (Image image : lstImage) {
                            if (uniqueSanPhamIDs.add(image.getSanPhamId().getId())) {
                                filteredImages.add(image);
                            }
                        }
                        model.addAttribute("lstImage", filteredImages);
                        model.addAttribute("gioHangChiTiet", lstGHCT);
                    }else {     //Giỏ hàng chi tiết chưa có sản phẩm
                        model.addAttribute("lstImage", null);
                        model.addAttribute("gioHangChiTiet", null);
                    }

                }
            }

            //          Thêm Chi tiết sản phẩm,

            GioHangChiTiet ghctNew = new GioHangChiTiet();
            ghctNew.setIdGioHang(gioHangRepository.getReferenceById(gioHangNew.getId()));
            ghctNew.setIdChiTietSanPham(chiTietSanPham);
            ghctNew.setIdChiTietSanPham(chiTietSanPham);
            ghctNew.setIdChiTietSanPham(chiTietSanPham);
            ghctNew.setNgayTao(ngayTao);
            ghctNew.setTrangThai(1);
            gioHangChiTietRepository.save(ghctNew);
            gioHangGlobal = gioHangNew;
            gioHangChiTietGlobal = ghctNew;
            return ResponseEntity.ok("Thêm thành công sản phẩm vào giỏ hàng");

//            KhachHang khachHang = khachHangRepository.getReferenceById(BigInteger.valueOf(10));
//            GioHang gioHangNew = gioHangRepository.getGioHangByIdKhachHang(khachHang.getId());
//
//            if(khachHang != null){
//                Optional<KhachHang> khachHangOpt = khachHangRepository.findKhachHangsByEmail(khachHang.getEmail())
//                        .stream()
//                        .filter(kh -> kh.getMatKhau().equals(khachHang.getMatKhau()))
//                        .findFirst();
//                model.addAttribute("KhachHang", khachHangOpt.get());
//            }else {
//                model.addAttribute("KhachHang", null);
//            }
//
//           if(gioHangNew == null){
//               gioHangNew = new GioHang();
//               gioHangNew.setIdKhachHang(khachHangRepository.getReferenceById(BigInteger.valueOf(10)));
//               gioHangNew.setTrangThai(1);
//               gioHangRepository.save(gioHangNew);
//               System.out.println("Chưa có giỏ hàng");
//           }
//            System.out.println("Có giỏ hàng");
//
////          Thêm Chi tiết sản phẩm,
//            ChiTietSanPham chiTietSanPham = ctspRepository.getChiTietSanPhamByIdSanPhamAndIdROMAndIdMauSac(request.getProductId(),request.getRomId(),request.getColorId());
////          Lấy ngày hiện tại
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            LocalDateTime currentDateTime = LocalDateTime.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            String ngayHienTai = currentDateTime.format(formatter);
//            Date ngayTao = new Date();
//            try {
//                ngayTao = dateFormat.parse(ngayHienTai);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            GioHangChiTiet ghctNew = new GioHangChiTiet();
//            ghctNew.setIdGioHang(gioHangRepository.getReferenceById(gioHangNew.getId()));
//            ghctNew.setIdChiTietSanPham(chiTietSanPham);
//            ghctNew.setIdChiTietSanPham(chiTietSanPham);
//            ghctNew.setIdChiTietSanPham(chiTietSanPham);
//            ghctNew.setNgayTao(ngayTao);
//            ghctNew.setTrangThai(1);
//            gioHangChiTietRepository.save(ghctNew);
//            gioHangGlobal = gioHangNew;
//            gioHangChiTietGlobal = ghctNew;
//            return ResponseEntity.ok("Thêm thành công sản phẩm vào giỏ hàng");
        } catch (Exception e) {
            e.printStackTrace(); // In chi tiết lỗi ra console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Thêm thất bại sản phẩm vào giỏ hàng.");
        }
    }


    @GetMapping("/gio-hang")
    public String getGioHang(
//            @PathVariable("idKhachHang") BigInteger idKhachHang,
            Model model,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ){
        GioHang gioHang = (GioHang) session.getAttribute("gioHang");
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        System.out.println("Khách hàng: "+khachHang.getTenKhachHang());
        if(khachHang == null){  //      đăng nhập
            GioHang gioHangNew =  new GioHang();
//            Thông tin khách hàng null
            model.addAttribute("KhachHang", null);
            gioHang = (GioHang) session.getAttribute("gioHang");
            if(gioHang == null){               //            Kiểm tra secction, giỏ hàng null
                gioHangNew.setTrangThai(1);
                gioHangRepository.save(gioHangNew);
                session.setAttribute("gioHang", gioHangNew);
                model.addAttribute("lstImage", null);
                model.addAttribute("gioHangChiTiet", null);
            }else {              //            Kiểm tra section, giỏ hàng khác null
                gioHangNew = gioHang;
                List<GioHangChiTiet> lstGHCT = gioHangChiTietRepository.getGioHangChiTietsByIdGioHang(gioHangNew.getId());
                if(lstGHCT.size() > 0){    //Giỏ hàng chi tiết đã có sản phẩm
                    List<Image> lstImage = imageRepository.findByTrangThai(2);
                    Set<BigInteger> uniqueSanPhamIDs = new HashSet<>();
                    List<Image> filteredImages = new ArrayList<>();
                    for (Image image : lstImage) {
                        if (uniqueSanPhamIDs.add(image.getSanPhamId().getId())) {
                            filteredImages.add(image);
                        }
                    }
                    model.addAttribute("lstImage", filteredImages);
                    model.addAttribute("gioHangChiTiet", lstGHCT);
                }else {     //Giỏ hàng chi tiết chưa có sản phẩm
                    model.addAttribute("lstImage", null);
                    model.addAttribute("gioHangChiTiet", null);
                }
            }
         }else {    //     Không đăng nhập
            GioHang gioHangNew = gioHangRepository.getGioHangByIdKhachHang(khachHang.getId());
//            Lấy thông tin khách hàng khi đăng nhập thành công
            Optional<KhachHang> khachHangOpt = khachHangRepository.findKhachHangsByEmail(khachHang.getEmail())
                    .stream()
                    .filter(kh -> kh.getMatKhau().equals(khachHang.getMatKhau()))
                    .findFirst();
            model.addAttribute("KhachHang", khachHangOpt.get());
//            Kiểm tra giỏ hàng
            if(gioHangRepository.getGioHangByIdKhachHang(khachHang.getId()) == null){ //Nếu chưa có giỏ hàng
                gioHangNew = new GioHang();
                model.addAttribute("lstImage", null);
                model.addAttribute("gioHangChiTiet", null);
                System.out.println("Chưa có giỏ hàng");
            }else {                                                             // Đã có giỏ hàng

                List<GioHangChiTiet> lstGHCT = gioHangChiTietRepository.getGioHangChiTietsByIdGioHang(gioHangNew.getId());
                if(lstGHCT.size() > 0){    //Giỏ hàng chi tiết đã có sản phẩm
                    List<Image> lstImage = imageRepository.findByTrangThai(2);
                    Set<BigInteger> uniqueSanPhamIDs = new HashSet<>();
                    List<Image> filteredImages = new ArrayList<>();
                    for (Image image : lstImage) {
                        if (uniqueSanPhamIDs.add(image.getSanPhamId().getId())) {
                            filteredImages.add(image);
                        }
                    }
                    model.addAttribute("lstImage", filteredImages);
                    model.addAttribute("gioHangChiTiet", lstGHCT);
                }else {     //Giỏ hàng chi tiết chưa có sản phẩm
                    model.addAttribute("lstImage", null);
                    model.addAttribute("gioHangChiTiet", null);
                }

            }
        }
//        if(gioHangRepository.getGioHangByIdKhachHang(idKhachHang) == null){
//            gioHangNew = new GioHang();
//            gioHangNew.setIdKhachHang(khachHangRepository.getReferenceById(BigInteger.valueOf(10)));
//            gioHangNew.setTrangThai(1);
//            gioHangRepository.save(gioHangNew);
//            model.addAttribute("lstImage", null);
//            model.addAttribute("gioHangChiTiet", null);
//            System.out.println("Chưa có giỏ hàng");
//        }else {
//            List<GioHangChiTiet> lstGHCT = gioHangChiTietRepository.getGioHangChiTietsByIdGioHang(gioHangNew.getId());
//            List<Image> lstImage = imageRepository.findByTrangThai(2);
//            Set<BigInteger> uniqueSanPhamIDs = new HashSet<>();
//            List<Image> filteredImages = new ArrayList<>();
//            for (Image image : lstImage) {
//                if (uniqueSanPhamIDs.add(image.getSanPhamId().getId())) {
//                    filteredImages.add(image);
//                }
//            }
//            model.addAttribute("lstImage", filteredImages);
//            model.addAttribute("gioHangChiTiet", lstGHCT);
//        }
//        List<GioHangChiTiet> lstGHCT = gioHangChiTietRepository.getGioHangChiTietsByIdGioHang(gioHangNew.getId());
//        List<Image> lstImage = imageRepository.findByTrangThai(2);
//        Set<BigInteger> uniqueSanPhamIDs = new HashSet<>();
//        List<Image> filteredImages = new ArrayList<>();
//        for (Image image : lstImage) {
//            if (uniqueSanPhamIDs.add(image.getSanPhamId().getId())) {
//                filteredImages.add(image);
//            }
//        }
//        model.addAttribute("lstImage", filteredImages);
//        model.addAttribute("gioHangChiTiet", lstGHCT);
        return "client/gio_hang";
    }

    @DeleteMapping("/delete-to-cart/{gioHangChiTietId}")
    public ResponseEntity<?> deleteFromCart(
            @PathVariable("gioHangChiTietId") BigInteger gioHangChiTietId)
    {
        try {
            GioHangChiTiet ghctDelete = gioHangChiTietRepository.getReferenceById(gioHangChiTietId);
            System.out.println("GHCT ID: "+ghctDelete.getId());
            gioHangChiTietRepository.deleteById(ghctDelete.getId());
            return ResponseEntity.ok(Map.of("status", "success", "message", "Xóa thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Xóa sản phẩm khỏi giỏ hàng thất bại!");
        }
    }


    @PostMapping("/your-controller-endpoint")
    public ResponseEntity<?> receiveSelectedProducts(@RequestBody List<Integer> productIds) {
        // Xử lý danh sách ID sản phẩm tại đây
        for (Integer productId : productIds) {
            System.out.println("Selected Product ID: " + productId);
        }
        // Trả về phản hồi cho client nếu cần
        return ResponseEntity.ok("Received product IDs successfully");
    }

    @PostMapping("/thanh-toan")
    public ResponseEntity<Map<String, Object>> addThanhToan(
            Model model,
            @RequestBody Map<String, Object> payload
//            @RequestParam(value = "idKhachHang", required = false) BigInteger idKhachHang,
//            @RequestParam("productIds") List<BigInteger> productIds
    ){
//        Không đăng nhập
//        Có đăng nhập
        // Lấy idKhachHang từ payload
        BigInteger idKhachHang = payload.containsKey("idKhachHang") ? new BigInteger(payload.get("idKhachHang").toString()) : null;

        List<BigInteger> productIds = ((List<?>) payload.get("productIds"))
                .stream()
                .map(Object::toString)
                .map(BigInteger::new)
                .collect(Collectors.toList());
        System.out.println("Có vào post");
        List<BigInteger> lstIDGioHang = productIds;
        List<ChiTietSanPham> lstCTSP = new ArrayList<>();
        List<Imei> lstImei = new ArrayList<>();
        BigDecimal tongHoaDon = new BigDecimal(0);
        for (BigInteger id: lstIDGioHang) {
            GioHangChiTiet ghct = gioHangChiTietRepository.getReferenceById(id);
            ChiTietSanPham ctsp = ctspRepository.getReferenceById(ghct.getIdChiTietSanPham().getId());
            tongHoaDon = tongHoaDon.add(ctsp.getGiaBan());
            lstCTSP.add(ctsp);
        }
        _tongHoaDon = tongHoaDon;
//
        lstImei_Global = new ArrayList<>();
        for (ChiTietSanPham ctsp : lstCTSP) {
            Pageable pageable = PageRequest.of(0, 1);
            List<Imei> imeis = imeiRepository.findImeiByIdChiTietSPAndTrangThai(ctsp.getId(), pageable);
            Imei firstImei = imeis.isEmpty() ? null : imeis.get(0);
            if (firstImei != null) {
                firstImei.setTrangThai(0);
                imeiRepository.save(firstImei);
                lstImei.add(firstImei);
                lstImei_Global.add(firstImei);
            } else {
                // Xử lý trường hợp không tìm thấy IMEI phù hợp
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Không tìm thấy IMEI phù hợp.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        _tongHoaDon = tongHoaDon;

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("redirectUrl", "/thanh-toan-gio-hang");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/thanh-toan-gio-hang")
    public String getThanhToan(
//            @PathVariable("idKhachHang") BigInteger idKhachHang,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ){
        model.addAttribute("lstImei", lstImei_Global);
        BigDecimal tongTien = lstImei_Global.stream()
                .map(imei -> imei.getIdChiTietSP().getGiaBan())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                model.addAttribute("tongTien",  tongTien);

//       Không đăng nhập
//        Có đăng nhập
        GioHang gioHang = (GioHang) session.getAttribute("gioHang");
        System.out.println("Giỏ hàng ID: "+gioHang.getId());
        KhachHang khachHangSection = (KhachHang) session.getAttribute("khachHang");
        System.out.println("Khách hàng ID: "+khachHangSection.getId());
        List<PhieuGiamGia> validVouchers = new ArrayList<>();
        if(khachHangSection == null){  //     Không đăng nhập
            validVouchers = phieuGiamGiaRepository.findValidVouchers(tongTien);
            model.addAttribute("lstDiaChiMacDinh", null);
            model.addAttribute("lstDiaChi", null);
            model.addAttribute("khachHang", null);
            model.addAttribute("DiaChi", null);
        }else {    //     đăng nhập
            KhachHang khachHang = khachHangRepository.getReferenceById(khachHangSection.getId());
            List<DiaChi> lstDiaChiMacDinh = diaChiRepository.findByIdKhachHangAndTrangThai3(khachHangSection.getId());
            List<DiaChi> lstDiaChi = diaChiRepository.findByIdKhachHangAndTrangThai1OrTrangThai3(khachHangSection.getId());
            validVouchers = phieuGiamGiaRepository.findValidVouchersNew(tongTien,khachHangSection.getId());
            model.addAttribute("lstDiaChiMacDinh", lstDiaChiMacDinh);
            model.addAttribute("lstDiaChi", lstDiaChi);
            model.addAttribute("khachHang", khachHang);
            model.addAttribute("DiaChi", new DiaChi());
        }
//

        _hoaDon= new HoaDon();
        _hoaDon.setTongTien(tongTien);
        _hoaDon.setIdKhachHang(khachHangSection);
        model.addAttribute("HoaDon", _hoaDon);
        model.addAttribute("PhieuGiamGia", validVouchers);

        return "client/thanh_toan1";
    }


    @PostMapping("/tao-hoa-don")
    @ResponseBody
    public ResponseEntity<?> completePayment(
            @RequestBody Map<String, Object> payload,
            HttpSession session,
            HttpServletRequest request
    )
    {
//      Thêm nhân viên


//        Lấy thông tin từ payload
        BigInteger idKhachHang = new BigInteger(payload.get("idKhachHang").toString());
        BigInteger phieuGiamGiaID = new BigInteger(payload.get("phieuGiamGiaID").toString());
        String hoTenNguoiNhan = payload.get("hoTenNguoiNhan").toString();
        String sdt = payload.get("sdt").toString();
        BigDecimal tongTienHang  = new BigDecimal(payload.get("tongTienHang").toString());
        BigDecimal tienDuocGiam  = new BigDecimal(payload.get("tienDuocGiam").toString());
        BigDecimal tongTienThanhToan  = new BigDecimal(payload.get("tongTienThanhToan").toString());
        System.out.println("Tổng tiền thanh toán: "+tongTienThanhToan);
        BigDecimal tienShip  = new BigDecimal(payload.get("tienShip").toString());
        String phuongThucThanhToan = payload.get("phuongThucThanhToan").toString();
//        List<BigInteger> imeiList = (List<BigInteger>) payload.get("imeiList");
        List<String> imeiIds = (List<String>) payload.get("imeiList");
        List<BigInteger> imeiList = imeiIds.stream()
                .map(BigInteger::new)
                .collect(Collectors.toList());

//          Lấy ngày hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String ngayHienTai = currentDateTime.format(formatter);
        Date ngayTao = new Date();
        try {
            ngayTao = dateFormat.parse(ngayHienTai);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Tạo hóa đơn
        SimpleDateFormat df = new SimpleDateFormat("yyMMdd");
        String maHD = "HD" + df.format(new Date()) + String.format("%02d", new Date().getYear() % 100) +
                hoaDonRepository.findAll().size();
        HoaDon hoaDonNew = new HoaDon();
        NhanVien nhanVien = nhanVienRepository.getReferenceById(BigInteger.valueOf(6));
        hoaDonNew.setIdNhanVien(nhanVien);
        hoaDonNew.setIdKhachHang(khachHangRepository.getReferenceById(idKhachHang));
        hoaDonNew.setMaHD(maHD);
        hoaDonNew.setNgayTao(ngayTao);
        if(phieuGiamGiaID != null){
            hoaDonNew.setIdPhieuGiamGia(phieuGiamGiaRepository.getReferenceById(phieuGiamGiaID));
        }
        hoaDonNew.setLoaiHoaDon(GIAO_HANG);
        hoaDonNew.setTenNguoiNhan(hoTenNguoiNhan);
        hoaDonNew.setSdtNguoiNhan(sdt);
        hoaDonNew.setTongTien(tongTienHang);
        hoaDonNew.setTongTienSauGiam(tongTienThanhToan);
        hoaDonNew.setPhiShip(tienShip);
        KhachHang kh = khachHangRepository.getReferenceById(idKhachHang);
        List<DiaChi> diaChi = diaChiRepository.findByIdKhachHangAndTrangThai3(idKhachHang);
        for (DiaChi dc:diaChi) {
            hoaDonNew.setDiaChi(dc.getDiaChiCuThe()+", "+dc.getPhuong()+", "+dc.getQuan()+", "+dc.getTinh());
        }
        hoaDonNew.setTrangThai(hoaDonRepository.HD_CHO_XAC_NHAN);
        hoaDonRepository.save(hoaDonNew);

//        Set trạng thái imei
        System.out.println("List im: "+imeiList.size());
        for (BigInteger idImei:imeiList) {
//            Set trạng thái imei
            Imei imei = imeiRepository.getReferenceById(idImei);
            imei.setTrangThai(2);
            imeiRepository.save(imei);
//            Tạo hóa đơn chi tiết
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setIdHoaDon(hoaDonNew);
            hoaDonChiTiet.setIdChiTietSP(imei.getIdChiTietSP());
            hoaDonChiTiet.setGia(imei.getIdChiTietSP().getGiaBan());
            hoaDonChiTiet.setTongTien(imei.getIdChiTietSP().getGiaBan());
            hoaDonChiTiet.setTrangThai(1);
            hoaDonChiTietRepository.save(hoaDonChiTiet);
//            Thêm vào bảng imei đã bán
            ImeiDaBan imeiDaBan = new ImeiDaBan();
            imeiDaBan.setMaImei(imei.getMaImei());
            imeiDaBan.setIdHoaDonCT(hoaDonChiTiet);
            imeiDaBanRepository.save(imeiDaBan);
        }
//        Cập nhật trạng thái phiếu giảm giá
        PhieuGiamGia phieuGiamGiaApply = phieuGiamGiaRepository.getReferenceById(phieuGiamGiaID);
        System.out.println("Phiếu giảm giá ID: "+phieuGiamGiaApply.getId());
        System.out.println("Phiếu giảm giá số lượng: "+phieuGiamGiaApply.getSoLuong());
        if(phieuGiamGiaApply.getTrangThaiSoLuong() == 0){
            Integer soLuong = phieuGiamGiaApply.getSoLuong() - 1;
            if(soLuong == 0){
                phieuGiamGiaApply.setTrangThai(DAKETTHUC);
            }
            phieuGiamGiaApply.setSoLuong(soLuong);
            phieuGiamGiaRepository.save(phieuGiamGiaApply);
        }
        // Lưu idHoaDon vào session
        session.setAttribute("idHoaDon", hoaDonNew.getId());
        LichSuHoaDon lichSuHoaDonGiaoHang = new LichSuHoaDon();
        if(phuongThucThanhToan.equals("vnPay")){
            ChiTietPhuongThucTT chiTietPhuongThucTT = new ChiTietPhuongThucTT();
            chiTietPhuongThucTT.setIdHoaDon(hoaDonNew);
            chiTietPhuongThucTT.setIdHinhThuc(hinhThucThanhToanRepository.getReferenceById(new BigInteger("3")));
            chiTietPhuongThucTT.setTrangThai(1);
            chiTietPhuongThucTT.setTongTien(tongTienThanhToan);
            chiTietPhuongThucTTRepository.save(chiTietPhuongThucTT);

            lichSuHoaDonGiaoHang.setIdHoaDon(hoaDonNew);
            lichSuHoaDonGiaoHang.setIdNhanVien(nhanVien);
            lichSuHoaDonGiaoHang.setThoiGian(new Date());
            lichSuHoaDonGiaoHang.setTrangThai(1);
            lichSuHoaDonGiaoHang.setGhiChu("Đặt hàng thành công");
            lichSuHoaDonRepository.save(lichSuHoaDonGiaoHang);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Thanh toán hoàn tất");
            response.put("idHoaDon", hoaDonNew.getId().toString());

            Integer orderTotal = hoaDonNew.getTongTienSauGiam().intValue();
            String orderInfo = "Thanh toan hoa don "+hoaDonNew.getMaHD();
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String bankBin = null;
            String vnpayUrl = vnpayService.createOrder(orderTotal, orderInfo, baseUrl, bankBin);
            System.out.println("Tiền thanh toán: "+orderTotal);
            System.out.println("Thông tin thanh toán: "+orderInfo);
            System.out.println("Đường dẫn: "+vnpayUrl);

            System.out.println("Số tiền thanh toán: "+orderTotal.toString());
            response.put("orderTotal",orderTotal.toString());
            response.put("orderInfo",orderInfo);
            response.put("baseUrl",baseUrl);
            response.put("bankBin",bankBin);
            response.put("vnpayUrl",vnpayUrl);
            response.put("idHoaDon",hoaDonNew.getId().toString());
            return ResponseEntity.ok(response);
        }else{
        lichSuHoaDonGiaoHang.setIdHoaDon(hoaDonNew);
        lichSuHoaDonGiaoHang.setIdNhanVien(nhanVien);
        lichSuHoaDonGiaoHang.setThoiGian(new Date());
        lichSuHoaDonGiaoHang.setTrangThai(1);
        lichSuHoaDonGiaoHang.setGhiChu("Đặt hàng thành công");
        lichSuHoaDonRepository.save(lichSuHoaDonGiaoHang);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Thanh toán hoàn tất");
            response.put("idHoaDon", hoaDonNew.getId().toString());
            return ResponseEntity.ok(response);
        }

    }

    @PostMapping("/submitOrder")
    public String submidOrder(@RequestParam("amount") String orderTotal,
                              @RequestParam("orderInfo") String orderInfo,
                              @RequestParam("bankBin") String bankBin,
                              HttpServletRequest request
    ){
        orderInfo = "Thanh toan hoa don "+orderInfo;
        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = vnpayService.createOrder(Integer.parseInt(orderTotal), orderInfo, baseUrl, bankBin);
        System.out.println("Tiền thanh toán: "+orderTotal);
        System.out.println("Thông tin thanh toán: "+orderInfo);
        return "redirect:" + vnpayUrl;
    }


    // Thêm địa chỉ cho khách hàng

    @PostMapping("/them-dia-chi/{KhachHangId}")
    @ResponseBody
    public ResponseEntity<?> create2(
            @Valid @ModelAttribute("DiaChi") DiaChiRequest diaChiRequest,
            BindingResult result,
            @PathVariable("KhachHangId") BigInteger khachHangID,
            Model model) throws Exception {
        try {
            if (result.hasErrors()) {
                List<String> errors = result.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.toList());
                return ResponseEntity.badRequest().body(errors);
            }

            // Kiểm tra nếu địa chỉ đã tồn tại
            List<DiaChi> existingDiaChi = diaChiRepository.findByDiaChiCuTheAndKhachHangId(
                    diaChiRequest.getDiaChiCuThe(), khachHangID);
            if (!existingDiaChi.isEmpty()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Địa chỉ đã tồn tại");
            }

            DiaChi diaChi = new DiaChi();
            diaChi.setIdKhachHang(khachHangID);
            diaChi.setDiaChiCuThe(diaChiRequest.getDiaChiCuThe());
            diaChi.setTinh(diaChiRequest.getTinh());
            diaChi.setQuan(diaChiRequest.getQuan());
            diaChi.setPhuong(diaChiRequest.getPhuong());
            diaChi.setTrangThai(ACTIVE);
            diaChiRepository.save(diaChi);

            List<DiaChi> updatedLstDiaChi = diaChiRepository.findAllBy(khachHangID, ACTIVE, DEFAULT);
            return ResponseEntity.ok(updatedLstDiaChi);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cap-nhat-dia-chi-mac-dinh/{index}")
    public String  updateDefaultAddress(
            @PathVariable("index") BigInteger index,
            RedirectAttributes redirectAttributes) {
        // Thực hiện cập nhật trạng thái của địa chỉ ở index được chọn
        System.out.println("Id địa chỉ mặc định "+index);
        DiaChi diaChiMacDinh = diaChiRepository.getReferenceById(index);
        List<DiaChi> lstDiaChiMacDinh = diaChiRepository.findByIdKhachHangAndTrangThai3(diaChiMacDinh.getIdKhachHang());
        for (DiaChi dc:lstDiaChiMacDinh) {
            dc.setTrangThai(ACTIVE);
            diaChiRepository.save(dc);
        }
        diaChiMacDinh.setTrangThai(DEFAULT);
        diaChiRepository.save(diaChiMacDinh);
        // Trả về phản hồi thành công hoặc lỗi (tùy vào logic của bạn)
        return "redirect:/thanh-toan/" + diaChiMacDinh.getIdKhachHang();
    }


    @GetMapping("/banks")
    public String getBanks(
            Model model,
            @RequestParam(value = "idHoaDon", required = false) String idHoaDon
    ) {
        List<Bank> banks = vnpayService.getBanks();
        model.addAttribute("banks", banks);
        model.addAttribute("idHoaDon", idHoaDon);
        return "client/paymentResult";
    }

//    @GetMapping("/don-hang-cua-toi")
//    public String donHangCuaToi(
//            Model model
//    ) {
//        List<Bank> banks = vnpayService.getBanks();
//        model.addAttribute("banks", banks);
//        return "client/don_hang_cua_toi";
//    }


    @GetMapping("/apply-voucher/{selectedVoucher}/{totalAmount}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> applyVoucher(
            @PathVariable("selectedVoucher") BigInteger selectedVoucher,
            @PathVariable("totalAmount") BigDecimal totalAmount)
    {
        BigDecimal soTienDuocGiam = new BigDecimal(0);
        BigDecimal totalPayAmount = totalAmount;

//        Kiểm tra là loại phiếu giảm giá nào
         if(selectedVoucher.equals(new BigInteger("0"))){
             System.out.println("selectedVoucher: "+selectedVoucher);
        soTienDuocGiam = new BigDecimal(0);
        totalPayAmount = totalAmount;
        }else {
             System.out.println("Có phiếu: "+selectedVoucher);
             PhieuGiamGia phieuGiamGiaApply = phieuGiamGiaRepository.getReferenceById(selectedVoucher);
            if(phieuGiamGiaApply.getLoaiGiamGia() == GIAMTHEOPHANTRAM){
                 Integer phamTramGiam = phieuGiamGiaApply.getPhanTramGiam();
                 BigDecimal phanTramGiam1 = new BigDecimal(phamTramGiam).divide(BigDecimal.valueOf(100));
                 soTienDuocGiam = totalPayAmount.multiply(phanTramGiam1);

                 System.out.println("Số tiền giảm "+soTienDuocGiam );
                 BigDecimal tienGiamToiDa = phieuGiamGiaApply.getSoTienDuocGiamToiDa();
                 if(soTienDuocGiam.compareTo(tienGiamToiDa) > 0){
                     soTienDuocGiam = tienGiamToiDa;
                     System.out.println("Số tiền giảm = số tiền giảm tối đa");
                 }
                 if(soTienDuocGiam.compareTo(tienGiamToiDa) < 0){
                     System.out.println("Số tiền giảm "+soTienDuocGiam );
                 }
                 totalPayAmount = totalAmount.subtract(soTienDuocGiam);
             }else if (phieuGiamGiaApply.getLoaiGiamGia() == GIAMTHEOTIEN){
                 soTienDuocGiam = phieuGiamGiaApply.getSoTienDuocGiam();
                 totalPayAmount = totalAmount.subtract(soTienDuocGiam);
             }
         }

        Map<String, String> response = new HashMap<>();
        response.put("discountAmount", String.format("%,.0f", soTienDuocGiam).replace(',', '.'));
        response.put("totalPayAmount", String.format("%,.0f", totalPayAmount).replace(',', '.'));
        response.put("tongTienSauGiam", String.format(String.valueOf(totalPayAmount)));
        response.put("tienDuocGiam", String.format(String.valueOf(soTienDuocGiam)));
        System.out.println("Tổng tiền sau giảm: "+totalPayAmount);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/vn-pay")
    public String home(){
        return "client/test";
    }


    @GetMapping("/vnpay-payment")
    public String handleVNPayReturn(
            @RequestParam Map<String, String> params,
            HttpSession session,
            Model model
    ) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String orderId = params.get("vnp_TxnRef");
        String totalPrice = params.get("vnp_Amount");
        String paymentTime = params.get("vnp_PayDate");
        String transactionId = params.get("vnp_TransactionNo");
        // Lấy idHoaDon từ session
        BigInteger idHoaDon = (BigInteger) session.getAttribute("idHoaDon");
        if ("00".equals(vnp_ResponseCode)) {
            // Thanh toán thành công
            // Thực hiện các hành động cần thiết, ví dụ: cập nhật trạng thái đơn hàng
            return "redirect:/don-hang-cua-toi/"+idHoaDon;
        } else {
            // Thanh toán thất bại hoặc bị hủy
            model.addAttribute("orderId", orderId);
            model.addAttribute("totalPrice", totalPrice);
            model.addAttribute("paymentTime", paymentTime);
            model.addAttribute("transactionId", transactionId);
            return "client/vnpay_orderfail"; // Chuyển hướng đến trang thông báo thanh toán thất bại
        }
    }

    @GetMapping("/phuong-thuc-thanh-toan")
    public ResponseEntity<Map<String, String>> handlePayment(
            @RequestParam("phuongThucThanhToan") String phuongThucThanhToan,
            @RequestParam(value = "bankCode", required = false) String bankCode,
            @RequestParam(value = "bankName", required = false) String bankName,
            @RequestParam(value = "bankBin", required = false) String bankBin,
            @RequestParam("idHoaDon") String idHoaDon
    ) {

        // Xử lý logic thanh toán ở đây
        HoaDon hoaDon = hoaDonRepository.getReferenceById(BigInteger.valueOf(Long.parseLong(idHoaDon)));
        // Trả về phản hồi
        Map<String, String> response = new HashMap<>();
        response.put("message", "Thanh toán hoàn tất");
        response.put("phuongThucThanhToan", phuongThucThanhToan);
        response.put("bankCode", bankCode);
        response.put("bankName", bankName);
        response.put("bankBin", bankBin);
        response.put("idHoaDon", idHoaDon);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/payment")
    public String payment(
            @RequestParam("phuongThucThanhToan") String phuongThucThanhToan,
            @RequestParam(value = "bankCode", required = false) String bankCode,
            @RequestParam(value = "bankName", required = false) String bankName,
            @RequestParam(value = "bankBin", required = false) String bankBin,
            @RequestParam("idHoaDon") String idHoaDon,
            Model model
    ) {

        // Xử lý logic thanh toán ở đây
        HoaDon hoaDon = hoaDonRepository.getReferenceById(BigInteger.valueOf(Long.parseLong(idHoaDon)));
        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("tienSauGiam", hoaDon.getTongTienSauGiam().intValue());
        model.addAttribute("bankCode", bankCode);
        model.addAttribute("bankName", bankName);
        model.addAttribute("bankBin", bankBin);


        return "client/vnpay_index";
    }

    @GetMapping("/don-hang-cua-toi/{idDonHang}")
    public String getDonHang(
            Model model,
//            @PathVariable("donHang") String maDonhang,
            @PathVariable("idDonHang") BigInteger idDonHang
    ){
        HoaDon hoaDon = hoaDonRepository.getReferenceById(idDonHang);
        Integer trangThaiDonHang = hoaDon.getTrangThai();

        List<HoaDonChiTiet> lstHDCT = hoaDonChiTietRepository.findAllByIdHoaDon(hoaDon);
        List<ChiTietSanPham> lstCTSP = new ArrayList<>();
        for (HoaDonChiTiet h: lstHDCT) {
            lstCTSP.add(h.getIdChiTietSP());
        }

        List<RomChiTietSanPham> lstSPHome = new ArrayList<>();
        for (ChiTietSanPham ctsp: lstCTSP) {
            RomChiTietSanPham sph = new RomChiTietSanPham();
            sph.setTenSanPham(ctsp.getIdSanPham().getTenSanPham());
            sph.setTenRom(ctsp.getIdROM().getTenRom());
            sph.setTenRam(ctsp.getIdRAM().getTen());
            sph.setTenMauSP(ctsp.getIdMauSac().getTenMauSac());
            sph.setGiaBan(ctsp.getGiaBan());
            List<String> lstAnh = imageRepository.findBySanPhamIdAndMauSacId(ctsp.getIdSanPham().getId(),ctsp.getIdMauSac().getId());
            String anhSP = (lstAnh != null && !lstAnh.isEmpty()) ? lstAnh.get(0) : null;
            sph.setAnhMSDaiDien(anhSP);

            lstSPHome.add(sph);
        }
//        ChiTietSanPham ctsp = hdct.getIdChiTietSP();
//        SanPham sanPham = ctsp.getIdSanPham();
//        MauSac mauSac = ctsp.getIdMauSac();
//
//        List<String> lstAnh = imageRepository.findBySanPhamIdAndMauSacId(sanPham.getId(),mauSac.getId());
//        String anhSP = (lstAnh != null && !lstAnh.isEmpty()) ? lstAnh.get(0) : null;
//
//        System.out.println("Tên ảnh sản phẩm: "+anhSP);

//        ChiTietPhuongThucTT chiTietPhuongThucTT = chiTietPhuongThucTTRepository.findByIdHinhThucAndIdHoaDon(hoaDon.getLoaiHoaDon(),hoaDon.getId());

        model.addAttribute("lstSPHome", lstSPHome);
        model.addAttribute("hoaDon", hoaDon);
        BigInteger idHinhThucThanhToan = hoaDonRepository.getIDHinhThucThanhToan(hoaDon.getId());
        model.addAttribute("hinhThucThanhToan", idHinhThucThanhToan);
        model.addAttribute("trangThaiDonHang", trangThaiDonHang);
        model.addAttribute("ngayTao", hoaDon.getNgayTao());
//        model.addAttribute("anhSP", anhSP);
        return "client/don_hang_cua_toi";
    }

    @GetMapping("/danh-sach-don-hang/{idKhachHang}")
    public String danhSachDonHang(
            @PathVariable("idKhachHang") BigInteger idKhachHang,
            HttpSession session,
            Model model
    ){
        KhachHang khachHang = (KhachHang) session.getAttribute("khachHang");
        if(khachHang == null){
            model.addAttribute("KhachHang", null);
        }else {
            Optional<KhachHang> khachHangOpt = khachHangRepository.findKhachHangsByEmail(khachHang.getEmail())
                    .stream()
                    .filter(kh -> kh.getMatKhau().equals(khachHang.getMatKhau()))
                    .findFirst();
            model.addAttribute("KhachHang", khachHangOpt.get());
            List<DanhSachDonHang> lstHoaDon = hoaDonRepository.getDonHang(khachHang.getId());
            model.addAttribute("lstHoaDon", lstHoaDon);
        }
        return "client/danh_sach_don_hang";
    }
}
