package com.example.demo.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.Entitys.*;
import com.example.demo.Repository.*;
import com.example.demo.Request.SanPhamRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Controller
@RequestMapping("san-pham")
public class SanPhamController {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 10;

    private final HangRepository hangRepository;
    private final CameraSauRepository cameraSauRepository;
    private final CameraTruocRepository cameraTruocRepository;
    private final ChipRepository chipRepository;
    private final HeDieuHanhRepository heDieuHanhRepository;
    private final ManHinhRepository manHinhRepository;
    private final PinRepository pinRepository;
    private final SimRepository simRepository;
    private final ROMRepository romRepository;
    private final RAMRepository ramRepository;
    private final MauSacRepository mauSacRepository;
    private final ChiTietSanPhamRepository ctspRepository;
    private final SanPhamRepository sanPhamRepository;
    private final ImeiRepository imeiRepository;
    private final ImageRepository imageRepository;
    private final SanPhamCameraSauRepo sanPhamCameraSauRepo;
    private final Cloudinary cloudinary;
    private final ExecutorService executorService;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

    public SanPhamController(HangRepository hangRepository, CameraSauRepository cameraSauRepository,
                             CameraTruocRepository cameraTruocRepository, ChipRepository chipRepository,
                             HeDieuHanhRepository heDieuHanhRepository, ManHinhRepository manHinhRepository,
                             PinRepository pinRepository, SimRepository simRepository, ROMRepository romRepository,
                             RAMRepository ramRepository, MauSacRepository mauSacRepository, ChiTietSanPhamRepository ctspRepository,
                             SanPhamRepository sanPhamRepository, ImeiRepository imeiRepository, ImageRepository imageRepository,
                             SanPhamCameraSauRepo sanPhamCameraSauRepo, Cloudinary cloudinary, ChiTietSanPhamRepository chiTietSanPhamRepository) {
        this.hangRepository = hangRepository;
        this.cameraSauRepository = cameraSauRepository;
        this.cameraTruocRepository = cameraTruocRepository;
        this.chipRepository = chipRepository;
        this.heDieuHanhRepository = heDieuHanhRepository;
        this.manHinhRepository = manHinhRepository;
        this.pinRepository = pinRepository;
        this.simRepository = simRepository;
        this.romRepository = romRepository;
        this.ramRepository = ramRepository;
        this.mauSacRepository = mauSacRepository;
        this.ctspRepository = ctspRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.imeiRepository = imeiRepository;
        this.imageRepository = imageRepository;
        this.sanPhamCameraSauRepo = sanPhamCameraSauRepo;
        this.cloudinary = cloudinary;
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    String success;
    String error;
    boolean checkAlter = false;

    SanPham sanPham = new SanPham();
    List<ROM> dsROM = new ArrayList<>();
    List<RAM> dsRAM = new ArrayList<>();
    List<MauSac> dsMauSac = new ArrayList<>();
    List<SanPham> lstSP = new ArrayList<>();
    List<ChiTietSanPham> lstCTSP = new ArrayList<>();
    List<List<String>> lstIM = new ArrayList<>(new ArrayList<>());
    String tenSanPham = "";
    List<SanPham_CameraSau> sanPhamCameraSaus = new ArrayList<>();


    //HOME
    @GetMapping("/index")
    public String index(@RequestParam("page") Optional<Integer> pageParam, Model model) {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 10);
        Page<SanPham> pageData = sanPhamRepository.findBy(pageable);
        model.addAttribute("data", pageData);
        model.addAttribute("lstPin", pinRepository.findAll());
        model.addAttribute("lstHang", hangRepository.findAll());
        model.addAttribute("lstHeDieuHanh", heDieuHanhRepository.findAll());
        model.addAttribute("lstManHinh", manHinhRepository.findAll());
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách sản phẩm");

        if (checkAlter == true) {
            model.addAttribute("success", this.success);
            this.success = null;
            checkAlter = false;
        }
        sanPhamCameraSaus.clear();

        dsMauSac.clear();
        dsRAM.clear();
        dsROM.clear();
        lstIM.clear();
        sanPham = new SanPham();
        lstCTSP.clear();
        return "admin/ql_chi_tiet_sp/index";
    }

    //MÀN HÌNH THÊM MỚI SẢN PHẨM
    @GetMapping("create-product")
    public String create(Model model) {
        model.addAttribute("lstHang", hangRepository.findByTrangThai(HangRepository.ACTIVE));
        model.addAttribute("lstCameraTruoc", cameraTruocRepository.findByTrangThai(CameraTruocRepository.ACTIVE));
        model.addAttribute("lstCameraSau", cameraSauRepository.findByTrangThai(CameraSauRepository.ACTIVE));
        model.addAttribute("lstChip", chipRepository.findByTrangThai(ChipRepository.ACTIVE));
        model.addAttribute("lstHeDieuHanh", heDieuHanhRepository.findByTrangThai(HeDieuHanhRepository.ACTIVE));
        model.addAttribute("lstManHinh", manHinhRepository.findByTrangThai(ManHinhRepository.ACTIVE));
        model.addAttribute("lstPin", pinRepository.findByTrangThai(PinRepository.ACTIVE));
        model.addAttribute("lstSim", simRepository.findByTrangThai(SimRepository.ACTIVE));
        model.addAttribute("lstRom", romRepository.findByTrangThai(ROMRepository.ACTIVE));
        model.addAttribute("lstRam", ramRepository.findByTrangThai(RAMRepository.ACTIVE));
        model.addAttribute("lstMauSac", mauSacRepository.findByTrangThai(MauSacRepository.ACTIVE));
        model.addAttribute("lstCTSP", lstCTSP);
        model.addAttribute("dsMauSac", dsMauSac);
        model.addAttribute("dsROM", dsROM);
        model.addAttribute("dsRAM", dsRAM);
        model.addAttribute("tenSanPham", sanPham.getTenSanPham());
        model.addAttribute("sanPham", new SanPhamRequest());
        model.addAttribute("lstIM", lstIM);
        if (checkAlter == true) {
            model.addAttribute("success", this.success);
            this.success = null;
            checkAlter = false;
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Thêm mới sản phẩm");
        return "admin/ql_chi_tiet_sp/create";
    }

    //MÀN HÌNH CẬP NHẬT SẢN PHẨM
    @GetMapping("view-update/{id}")
    public String ViewUpdate(@PathVariable("id") SanPham sp, Model model) {
        SanPhamRequest sanPham = new SanPhamRequest();
        sanPham.setId(sp.getId());
        sanPham.setMaSanPham(sp.getMaSanPham());
        sanPham.setTenSanPham(sp.getTenSanPham());
        sanPham.setIdCameraTruoc(sp.getIdCameraTruoc().getId());
        sanPham.setIdHeDieuHanh(sp.getIdHeDieuHanh().getId());
        sanPham.setIdManHinh(sp.getIdManHinh().getId());
        sanPham.setIdHang(sp.getIdHang().getId());
        sanPham.setIdSim(sp.getIdSim().getId());
        sanPham.setIdPin(sp.getIdPin().getId());
        sanPham.setIdChip(sp.getIdChip().getId());

        List<Image> listImage = imageRepository.findAllBySanPhamId(sp.getId());
        List<BigInteger> idMauSacs = listImage.stream().map(x -> x.getMauSacId()).collect(Collectors.toList());
        List<MauSac> mauSacs = mauSacRepository.findAllById(idMauSacs);
        System.out.println(mauSacs);
        model.addAttribute("listImage", listImage);
        model.addAttribute("dsMauSac", mauSacs);
        model.addAttribute("lstHang", hangRepository.findByTrangThai(HangRepository.ACTIVE));
        model.addAttribute("lstCameraTruoc", cameraTruocRepository.findByTrangThai(CameraTruocRepository.ACTIVE));
        model.addAttribute("lstCameraSau", cameraSauRepository.findByTrangThai(CameraSauRepository.ACTIVE));
        model.addAttribute("lstChip", chipRepository.findByTrangThai(ChipRepository.ACTIVE));
        model.addAttribute("lstHeDieuHanh", heDieuHanhRepository.findByTrangThai(HeDieuHanhRepository.ACTIVE));
        model.addAttribute("lstManHinh", manHinhRepository.findByTrangThai(ManHinhRepository.ACTIVE));
        model.addAttribute("lstPin", pinRepository.findByTrangThai(PinRepository.ACTIVE));
        model.addAttribute("lstSim", simRepository.findByTrangThai(SimRepository.ACTIVE));
        model.addAttribute("lstSanPhamCameraSau", sanPhamCameraSauRepo.findBySanPhamId(sp.getId()));
        model.addAttribute("sanPham", sanPham);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Cập nhật sản phẩm");
        if (checkAlter == true) {
            model.addAttribute("success", this.success);
            this.success = null;
            checkAlter = false;
        }
        return "admin/ql_chi_tiet_sp/update";
    }

    //CẬP NHẬT SẢN PHẨM
    @Transactional
    @PostMapping("/update/{id}")
    public String update(@Valid @ModelAttribute("sanPham") SanPhamRequest sanPhamRequest, BindingResult result,
                         @PathVariable("id") BigInteger id, Model model,
                         @RequestParam(value = "ImageId", required = false) List<BigInteger> ImageIds,
                         @RequestParam(value = "ImageIdByMauSac", required = false) List<BigInteger> ImageIdByMauSac,
                         @RequestParam(value = "image", required = false) List<MultipartFile> images,
                         MultipartHttpServletRequest request
    ) {
        if (result.hasErrors()) {
            System.out.println("co loi");   List<Image> listImage = imageRepository.findAllBySanPhamId(id);
            List<BigInteger> idMauSacs = listImage.stream().map(x -> x.getMauSacId()).collect(Collectors.toList());
            List<MauSac> mauSacs = mauSacRepository.findAllById(idMauSacs);
            System.out.println(mauSacs);
            model.addAttribute("listImage", listImage);
            model.addAttribute("dsMauSac", mauSacs);
            model.addAttribute("lstHang", hangRepository.findByTrangThai(HangRepository.ACTIVE));
            model.addAttribute("lstCameraTruoc", cameraTruocRepository.findByTrangThai(CameraTruocRepository.ACTIVE));
            model.addAttribute("lstCameraSau", cameraSauRepository.findByTrangThai(CameraSauRepository.ACTIVE));
            model.addAttribute("lstChip", chipRepository.findByTrangThai(ChipRepository.ACTIVE));
            model.addAttribute("lstHeDieuHanh", heDieuHanhRepository.findByTrangThai(HeDieuHanhRepository.ACTIVE));
            model.addAttribute("lstManHinh", manHinhRepository.findByTrangThai(ManHinhRepository.ACTIVE));
            model.addAttribute("lstPin", pinRepository.findByTrangThai(PinRepository.ACTIVE));
            model.addAttribute("lstSim", simRepository.findByTrangThai(SimRepository.ACTIVE));
            model.addAttribute("lstSanPhamCameraSau", sanPhamCameraSauRepo.findBySanPhamId(id));
            model.addAttribute("sanPham", sanPhamRequest);
            model.addAttribute("message_title1", "Quản lý sản phẩm");
            model.addAttribute("message_title2", "Cập nhật sản phẩm");
            return "admin/ql_chi_tiet_sp/update";
        }

        SanPham spCheck = sanPhamRepository.findByTenSanPhamIsLike(sanPhamRequest.getTenSanPham().trim());
        if (spCheck != null && !spCheck.getId().equals(id)) {
            List<Image> listImage = imageRepository.findAllBySanPhamId(id);
            List<BigInteger> idMauSacs = listImage.stream().map(x -> x.getMauSacId()).collect(Collectors.toList());
            List<MauSac> mauSacs = mauSacRepository.findAllById(idMauSacs);
            System.out.println(mauSacs);
            model.addAttribute("listImage", listImage);
            model.addAttribute("dsMauSac", mauSacs);
            model.addAttribute("messageTen", "Trùng tên sản phẩm!");
            model.addAttribute("lstHang", hangRepository.findByTrangThai(HangRepository.ACTIVE));
            model.addAttribute("lstCameraTruoc", cameraTruocRepository.findByTrangThai(CameraTruocRepository.ACTIVE));
            model.addAttribute("lstCameraSau", cameraSauRepository.findByTrangThai(CameraSauRepository.ACTIVE));
            model.addAttribute("lstChip", chipRepository.findByTrangThai(ChipRepository.ACTIVE));
            model.addAttribute("lstHeDieuHanh", heDieuHanhRepository.findByTrangThai(HeDieuHanhRepository.ACTIVE));
            model.addAttribute("lstManHinh", manHinhRepository.findByTrangThai(ManHinhRepository.ACTIVE));
            model.addAttribute("lstPin", pinRepository.findByTrangThai(PinRepository.ACTIVE));
            model.addAttribute("lstSim", simRepository.findByTrangThai(SimRepository.ACTIVE));
            model.addAttribute("lstSanPhamCameraSau", sanPhamCameraSauRepo.findBySanPhamId(id));
            model.addAttribute("sanPham", sanPhamRequest);
            model.addAttribute("message_title1", "Quản lý sản phẩm");
            model.addAttribute("message_title2", "Cập nhật sản phẩm");
            return "admin/ql_chi_tiet_sp/update";
        }


        // xoá ảnh chính
        List<Image> imageList = imageRepository.findAllBySanPhamId(sanPhamRequest.getId());
        List<Image> idImageAnhChinhRepo = imageList.stream().filter(x -> x.getMauSacId() == null).collect(Collectors.toList());
        List<Image> idImageMauSacRepo = imageList.stream().filter(x -> x.getMauSacId() != null).collect(Collectors.toList());
        List<BigInteger> imageRemove = new ArrayList<>();

        if (ImageIds == null) {
            imageRemove.addAll(idImageAnhChinhRepo.stream()
                    .map(Image::getId)
                    .collect(Collectors.toList()));
        }
        else {
            imageRemove.addAll(idImageAnhChinhRepo.stream()
                    .map(Image::getId)
                    .filter(imageId -> !ImageIds.contains(imageId))
                    .collect(Collectors.toList()));
        }



        sanPhamCameraSauRepo.deleteAllBySanPhamId(id);
        for (BigInteger req : sanPhamRequest.getIdCameraSau()) {
            SanPham_CameraSau obj = new SanPham_CameraSau();
            if (req.equals(sanPhamRequest.getIdCameraChinhSau())) {
                obj.setTrangThai(2);
            } else {
                obj.setTrangThai(1);
            }
            obj.setCameraSauId(req);
            sanPhamCameraSaus.add(obj);
        }

        List<SanPham_CameraSau> sanPham_cameraSauSave = new ArrayList<>();
        for (SanPham_CameraSau sanPham_cameraSau : sanPhamCameraSaus) {
            sanPham_cameraSau.setSanPhamId(sanPhamRequest.getId());
            sanPham_cameraSauSave.add(sanPham_cameraSau);
        }

        sanPhamCameraSauRepo.saveAll(sanPham_cameraSauSave);
        sanPhamCameraSaus.clear();

        Optional<SanPham> optionalSanPham = sanPhamRepository.findById(id);
        SanPham sanPham1 = optionalSanPham.get();
        sanPham1.setTenSanPham(sanPhamRequest.getTenSanPham());
        sanPham1.setIdChip(new Chip(sanPhamRequest.getIdChip(), null, null));
        sanPham1.setIdCameraTruoc(new CameraTruoc(sanPhamRequest.getIdCameraTruoc(), null, null));
        sanPham1.setIdHeDieuHanh(new HeDieuHanh(sanPhamRequest.getIdHeDieuHanh(), null, null));
        sanPham1.setIdManHinh(new ManHinh(sanPhamRequest.getIdManHinh(), null, null));
        sanPham1.setIdPin(new Pin(sanPhamRequest.getIdPin(), null, null));
        sanPham1.setIdSim(new Sim(sanPhamRequest.getIdSim(), null, null));
        sanPham1.setNgaySua(new Date());
        sanPhamRepository.save(sanPham1);




        if (ImageIdByMauSac == null) {
            imageRemove.addAll(idImageMauSacRepo.stream()
                    .map(Image::getId)
                    .collect(Collectors.toList()));
        } else {
            imageRemove.addAll(idImageMauSacRepo.stream()
                    .map(Image::getId)
                    .filter(imageId -> !ImageIdByMauSac.contains(imageId))
                    .collect(Collectors.toList()));
        }

        // Xóa ảnh khỏi Cloudinary
        idImageAnhChinhRepo.stream()
                .filter(image -> imageRemove.contains(image.getId()))
                .forEach(image -> {
                    try {
                        deleteImageByPublicId(image.getTenAnh());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        idImageMauSacRepo.stream()
                .filter(image -> imageRemove.contains(image.getId()))
                .forEach(image -> {
                    try {
                        deleteImageByPublicId(image.getTenAnh());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        // Xóa ảnh khỏi repository
        imageRepository.deleteAllById(imageRemove);


        Map<String, List<MultipartFile>> fileMap = request.getMultiFileMap();
        SanPham sanPham = new SanPham();
        sanPham.setId(id);
        // Lưu ảnh chính
        List<Image> listImage = new ArrayList<>();
        if (images != null && !images.get(0).getOriginalFilename().equals("")) {
            images.parallelStream().forEach(file -> {
                try {
                    Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                    Image image = new Image();
                    image.setTenAnh((String) r.get("secure_url"));
                    System.out.println("Tên ảnh chính: " + image.getTenAnh());
                    listImage.add(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }


        List<Image> imageTotals = new ArrayList<>();
        for (Image i : listImage) {
            Image newImage = new Image();
            newImage.setSanPhamId(sanPham);
            newImage.setTenAnh(i.getTenAnh());
            newImage.setTrangThai(1);
            imageTotals.add(newImage);
        }
        imageRepository.saveAll(imageTotals);

        // Lưu ảnh theo màu
        String prefix = "images";
        List<Image> listImageByColor = new ArrayList<>();

        fileMap.entrySet().parallelStream().forEach(entry -> {
            String key = entry.getKey();
            List<MultipartFile> files = entry.getValue();
            if (!files.get(0).getOriginalFilename().equals("")) {


                files.forEach(file -> {
                    if (!file.getName().equals("image")) {
                        BigInteger idMauSac = new BigInteger(file.getName().substring(prefix.length()));
                        try {
                            Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                            Image image = new Image();
                            image.setTenAnh((String) r.get("secure_url"));
                            image.setMauSacId(idMauSac);
                            listImageByColor.add(image);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        List<Image> lstImageTotals = new ArrayList<>();
        for (Image i : listImageByColor) {
            Image newImage = new Image();
            newImage.setSanPhamId(sanPham);
            newImage.setTenAnh(i.getTenAnh());
            newImage.setTrangThai(1);
            newImage.setMauSacId(i.getMauSacId());
            lstImageTotals.add(newImage);
        }
        imageRepository.saveAll(lstImageTotals);

        this.success = "Cập nhật sản phẩm thành công";
        checkAlter = true;

        return "redirect:/san-pham/index";
    }

    public void loadValidate(Model model) {
        model.addAttribute("lstHang", hangRepository.findByTrangThai(HangRepository.ACTIVE));
        model.addAttribute("lstCameraTruoc", cameraTruocRepository.findByTrangThai(CameraTruocRepository.ACTIVE));
        model.addAttribute("lstCameraSau", cameraSauRepository.findByTrangThai(CameraSauRepository.ACTIVE));
        model.addAttribute("lstChip", chipRepository.findByTrangThai(ChipRepository.ACTIVE));
        model.addAttribute("lstHeDieuHanh", heDieuHanhRepository.findByTrangThai(HeDieuHanhRepository.ACTIVE));
        model.addAttribute("lstManHinh", manHinhRepository.findByTrangThai(ManHinhRepository.ACTIVE));
        model.addAttribute("lstPin", pinRepository.findByTrangThai(PinRepository.ACTIVE));
        model.addAttribute("lstSim", simRepository.findByTrangThai(SimRepository.ACTIVE));
        model.addAttribute("lstRom", romRepository.findByTrangThai(ROMRepository.ACTIVE));
        model.addAttribute("lstRam", ramRepository.findByTrangThai(RAMRepository.ACTIVE));
        model.addAttribute("lstMauSac", mauSacRepository.findByTrangThai(MauSacRepository.ACTIVE));
        model.addAttribute("lstCTSP", lstCTSP);
        model.addAttribute("dsMauSac", dsMauSac);
        model.addAttribute("dsROM", dsROM);
        model.addAttribute("dsRAM", dsRAM);
        model.addAttribute("lstIM", lstIM);
    }

    //LƯU SẢN PHẨM - CHI TIẾT SẢN PHẨM VÀO LIST
    @PostMapping("store")
    public String store(
            @Valid @ModelAttribute("sanPham") SanPhamRequest sanPhamRequest,
            BindingResult result,
            @RequestParam(value = "idRAM", defaultValue = "") List<BigInteger> lstIdRam,
            @RequestParam(value = "idROM", defaultValue = "") List<BigInteger> lstIdRom,
            @RequestParam(value = "idMauSac", defaultValue = "") List<BigInteger> lstIdMauSac,
            Model model
    ) throws IOException {

        if (result.hasErrors()) {
            System.out.println("co loi");
            model.addAttribute("listIdRamChecked", lstIdRam);
            model.addAttribute("listIdRomChecked", lstIdRom);
            model.addAttribute("listIdMauSacChecked", lstIdMauSac);
            model.addAttribute("lstSanPhamCameraSau", sanPhamRequest.getIdCameraSau());
            model.addAttribute("idCameraChinhSau", sanPhamRequest.getIdCameraChinhSau());
            loadValidate(model);
            model.addAttribute("message_title1", "Quản lý sản phẩm");
            model.addAttribute("message_title2", "Thêm mới sản phẩm");
            return "admin/ql_chi_tiet_sp/create";
        }

        if (sanPhamRepository.findByTenSanPhamIsLike(sanPhamRequest.getTenSanPham().trim()) != null) {
            model.addAttribute("messageTen", "Trùng tên sản phẩm!");
            model.addAttribute("listIdRamChecked", lstIdRam);
            model.addAttribute("listIdRomChecked", lstIdRom);
            model.addAttribute("listIdMauSacChecked", lstIdMauSac);
            model.addAttribute("lstSanPhamCameraSau", sanPhamRequest.getIdCameraSau());
            model.addAttribute("idCameraChinhSau", sanPhamRequest.getIdCameraChinhSau());
            loadValidate(model);
            model.addAttribute("message_title1", "Quản lý sản phẩm");
            model.addAttribute("message_title2", "Thêm mới sản phẩm");
            return "admin/ql_chi_tiet_sp/create";
        }
        if (lstIdRam.isEmpty()) {
            model.addAttribute("messageRam", "Chọn Ram!");
            model.addAttribute("listIdRamChecked", lstIdRam);
            model.addAttribute("listIdRomChecked", lstIdRom);
            model.addAttribute("listIdMauSacChecked", lstIdMauSac);
            model.addAttribute("lstSanPhamCameraSau", sanPhamRequest.getIdCameraSau());
            model.addAttribute("idCameraChinhSau", sanPhamRequest.getIdCameraChinhSau());
            loadValidate(model);
            model.addAttribute("message_title1", "Quản lý sản phẩm");
            model.addAttribute("message_title2", "Thêm mới sản phẩm");
            return "admin/ql_chi_tiet_sp/create";
        }
        if (lstIdRom.isEmpty()) {
            model.addAttribute("messageRom", "Chọn Rom!");
            model.addAttribute("listIdRamChecked", lstIdRam);
            model.addAttribute("listIdRomChecked", lstIdRom);
            model.addAttribute("listIdMauSacChecked", lstIdMauSac);
            model.addAttribute("lstSanPhamCameraSau", sanPhamRequest.getIdCameraSau());
            model.addAttribute("idCameraChinhSau", sanPhamRequest.getIdCameraChinhSau());
            loadValidate(model);
            model.addAttribute("message_title1", "Quản lý sản phẩm");
            model.addAttribute("message_title2", "Thêm mới sản phẩm");
            return "admin/ql_chi_tiet_sp/create";
        }
        if (lstIdMauSac.isEmpty()) {
            model.addAttribute("messageMauSac", "Chọn Màu sắc!");
            model.addAttribute("listIdRamChecked", lstIdRam);
            model.addAttribute("listIdRomChecked", lstIdRom);
            model.addAttribute("listIdMauSacChecked", lstIdMauSac);
            loadValidate(model);
            model.addAttribute("message_title1", "Quản lý sản phẩm");
            model.addAttribute("message_title2", "Thêm mới sản phẩm");
            return "admin/ql_chi_tiet_sp/create";
        }
        String tenSanPham = sanPhamRequest.getTenSanPham();
        BigInteger idHeDieuHanh = sanPhamRequest.getIdHeDieuHanh();
        BigInteger idManHinh = sanPhamRequest.getIdManHinh();
        BigInteger idHang = sanPhamRequest.getIdHang();
        BigInteger idCameraTruoc = sanPhamRequest.getIdCameraTruoc();
        List<BigInteger> idCameraSau = sanPhamRequest.getIdCameraSau();
        BigInteger idCameraChinhSau = sanPhamRequest.getIdCameraChinhSau();
        BigInteger idSim = sanPhamRequest.getIdSim();
        BigInteger idPin = sanPhamRequest.getIdPin();
        BigInteger idChip = sanPhamRequest.getIdChip();

        SanPham sanPhamRq = new SanPham();
        sanPhamRq.setTenSanPham(tenSanPham);
        HeDieuHanh hdh = new HeDieuHanh();
        hdh.setId(idHeDieuHanh);
        sanPhamRq.setIdHeDieuHanh(hdh);
        ManHinh mh = new ManHinh();
        mh.setId(idManHinh);
        sanPhamRq.setIdManHinh(mh);
        Hang hang = new Hang();
        hang.setId(idHang);
        sanPhamRq.setIdHang(hang);
        CameraTruoc cameraTruoc = new CameraTruoc();
        cameraTruoc.setId(idCameraTruoc);
        sanPhamRq.setIdCameraTruoc(cameraTruoc);

        for (BigInteger req : idCameraSau) {
            SanPham_CameraSau obj = new SanPham_CameraSau();
            if (req.equals(idCameraChinhSau)) {
                obj.setTrangThai(2);
            }
            if (!req.equals(idCameraChinhSau)) {
                obj.setTrangThai(1);
            }
            obj.setCameraSauId(req);
            sanPhamCameraSaus.add(obj);
        }

        Sim sim = new Sim();
        sim.setId(idSim);
        sanPhamRq.setIdSim(sim);
        Pin pin = new Pin();
        pin.setId(idPin);
        sanPhamRq.setIdPin(pin);
        Chip chip = new Chip();
        chip.setId(idChip);
        sanPhamRq.setIdChip(chip);
        for (BigInteger id : lstIdRam) {
            RAM ram = ramRepository.findById(id).get();
            dsRAM.add(ram);
        }

        for (BigInteger id : lstIdRom) {
            ROM rom = romRepository.findById(id).get();
            dsROM.add(rom);
        }

        for (BigInteger id : lstIdMauSac) {
            MauSac mauSac = mauSacRepository.findById(id).get();
            dsMauSac.add(mauSac);
        }

//        BigInteger count = BigInteger.valueOf(0);
        for (BigInteger idRam : lstIdRam) {
            for (BigInteger idRom : lstIdRom) {
                for (BigInteger idMauSac : lstIdMauSac) {
                    ChiTietSanPham ctsp = new ChiTietSanPham();
                    SanPham sp = new SanPham();
                    sp.setTenSanPham(sanPhamRq.getTenSanPham());
                    RAM ram = new RAM();
                    ram.setId(idRam);
                    ROM rom = new ROM();
                    rom.setId(idRom);
                    MauSac mauSac = new MauSac();
                    mauSac.setId(idMauSac);

//                    count = count.add(BigInteger.valueOf(1));
//                    BigInteger maxId = BigInteger.valueOf(0);
//                    Optional<ChiTietSanPham> maxIdctspn = ctspRepository.findMaId();
//                    if (maxIdctspn.isPresent()) {
//                        maxId = count.add(maxIdctspn.get().getId());
//                    }
//                    if (maxIdctspn.isEmpty()) {
//                        maxId = count;
//                    }
//                    ctsp.setMaChiTietSanPham("SPCT" + maxId);
                    ctsp.setIdSanPham(sp);
                    ctsp.setIdROM(rom);
                    ctsp.setIdRAM(ram);
                    ctsp.setIdMauSac(mauSac);
                    ctsp.setTrangThai(ChiTietSanPhamRepository.ACTIVE);
                    lstCTSP.add(ctsp);
                    List<String> lstIMForCTSP = new ArrayList<>();
                    lstIM.add(lstIMForCTSP);
                    this.success = "Thêm sản phẩm thành công";
                    checkAlter = true;
                }
            }
        }

//        count = BigInteger.valueOf(0);
        Optional<SanPham> maxIdSP = sanPhamRepository.findMaId();
        BigInteger maxId = maxIdSP.isPresent() ? maxIdSP.get().getId().add(BigInteger.ONE) : BigInteger.ONE;
        sanPhamRq.setMaSanPham("SP" + maxId);
        sanPhamRq.setTrangThai(1);
        sanPham = sanPhamRq;
        return "redirect:/san-pham/create-product";
    }

    //MÀN HÌNH THÊM MỚI - XOÁ CHI TIẾT SẢN PHẨM
    @GetMapping("/delete/{idRAM}/{idROM}/{idMauSac}")
    public String delete(
            @PathVariable("idRAM") RAM idRAM,
            @PathVariable("idROM") ROM idROM,
            @PathVariable("idMauSac") MauSac idMauSac) {

        for (int i = 0; i < lstCTSP.size(); i++) {
            if (lstCTSP.get(i).getIdRAM().getId().equals(idRAM.getId()) &&
                    lstCTSP.get(i).getIdROM().getId().equals(idROM.getId()) &&
                    lstCTSP.get(i).getIdMauSac().getId().equals(idMauSac.getId())) {
                System.out.println(i);
                lstCTSP.remove(i);

                for (int j = 0; j < lstIM.size(); j++) {
                    lstIM.remove(i);
                    break;
                }
            }
        }
        this.success = "Xoá phiên bản thành công";
        checkAlter = true;
        return "redirect:/san-pham/create-product";
    }

    //MÀN HÌNH THÊM MỚI - XOÁ PHIÊN BẢN
    @GetMapping("/delete/{idRAM}/{idROM}")
    public String deletePB(
            @PathVariable("idRAM") RAM idRAM,
            @PathVariable("idROM") ROM idROM) {

        List<ChiTietSanPham> removeCTSP = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < lstCTSP.size(); i++) {
            if (lstCTSP.get(i).getIdRAM().getId().equals(idRAM.getId()) &&
                    lstCTSP.get(i).getIdROM().getId().equals(idROM.getId())) {
                removeCTSP.add(lstCTSP.get(i));
                list.add(i);
            }
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            int indexToRemove = list.get(i);
            lstIM.remove(indexToRemove);
        }
        lstCTSP.removeAll(removeCTSP);
        this.success = "Xoá phiên bản thành công";
        checkAlter = true;
        return "redirect:/san-pham/create-product";
    }

    // LƯU SẢN PHẨM - CHI TIẾT SẢN PHẨM VÀO DB
    @PostMapping("/updatePrice")
    public String updatePrice(@RequestParam("price") List<BigDecimal> lstPrice,
                              @RequestParam("idRam") List<BigInteger> idRams,
                              @RequestParam("idRom") List<BigInteger> idRoms,
                              @RequestParam("idMauSac") List<BigInteger> idMauSacs,
                              @RequestParam("image") List<MultipartFile> images,
                              MultipartHttpServletRequest request
    ) throws IOException {

        sanPham.setNgayTao(new Date());
        sanPham.setNguoiTao(null);
        sanPham = sanPhamRepository.save(sanPham);

        List<SanPham_CameraSau> sanPham_cameraSauSave = new ArrayList<>();
        for (SanPham_CameraSau sanPham_cameraSau : sanPhamCameraSaus) {
            sanPham_cameraSau.setSanPhamId(sanPham.getId());
            sanPham_cameraSauSave.add(sanPham_cameraSau);
        }
        sanPhamCameraSauRepo.saveAll(sanPham_cameraSauSave);
        sanPhamCameraSaus.clear();

        for (int i = 0; i < lstCTSP.size(); i++) {
            lstCTSP.get(i).setGiaBan(lstPrice.get(i));
            lstCTSP.get(i).setIdSanPham(sanPham);
            lstCTSP.get(i).setNgayTao(new Date());
            lstCTSP.get(i).setNguoiTao(null);
        }


        //        count = BigInteger.valueOf(0);
//                    count = count.add(BigInteger.valueOf(1));
//                    BigInteger maxId = BigInteger.valueOf(0);
//                    Optional<ChiTietSanPham> maxIdctspn = ctspRepository.findMaId();
//                    if (maxIdctspn.isPresent()) {
//                        maxId = count.add(maxIdctspn.get().getId());
//                    }
//                    if (maxIdctspn.isEmpty()) {
//                        maxId = count;
//                    }
//                    ctsp.setMaChiTietSanPham("SPCT" + maxId);
        //        BigInteger count = BigInteger.valueOf(0);

        BigInteger count = BigInteger.valueOf(0);
                BigInteger maxId = BigInteger.valueOf(0);
                for (ChiTietSanPham chiTietSanPham: lstCTSP){
                    Optional<ChiTietSanPham> maxIdctspn = ctspRepository.findMaId();

                    count = count.add(BigInteger.valueOf(1));
                    if (maxIdctspn.isPresent()) {
                                maxId = count.add(maxIdctspn.get().getId());
                            }
                            if (maxIdctspn.isEmpty()) {
                                maxId = count;
                            }
                    chiTietSanPham.setMaChiTietSanPham("CTSP"+maxId);
        }
         count = BigInteger.valueOf(0);


        List<ChiTietSanPham> ctspSave = ctspRepository.saveAll(lstCTSP);

        List<Imei> lstImei = new ArrayList<>();
        BigInteger idSanPham = new BigInteger(String.valueOf(0));
        for (int i = 0; i < lstCTSP.size(); i++) {
            ChiTietSanPham ctsp = lstCTSP.get(i);
            SanPham sanPham1 = new SanPham();
            idSanPham = sanPham.getId();
            sanPham1.setId(sanPham.getId());
            RAM ram = new RAM();
            ram.setId(lstCTSP.get(i).getIdRAM().getId());
            ROM rom = new ROM();
            rom.setId(lstCTSP.get(i).getIdROM().getId());
            MauSac mauSac = new MauSac();
            mauSac.setId(lstCTSP.get(i).getIdMauSac().getId());
            ChiTietSanPham findCTSP = ctspRepository.findByIdRAMAndIdROMAndIdMauSacAndIdSanPham(ram, rom, mauSac, sanPham1);
            for (int j = 0; j < lstIM.get(i).size(); j++) {
                Imei imei = new Imei();
                imei.setIdChiTietSP(findCTSP);
                imei.setMaImei(lstIM.get(i).get(j));
                imei.setTrangThai(1);
                lstImei.add(imei);
            }
        }
        imeiRepository.saveAll(lstImei);

        Map<String, List<MultipartFile>> fileMap = request.getMultiFileMap();

        // Lưu ảnh chính
        List<Image> listImage = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            images.parallelStream().forEach(file -> {
                try {
                    Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                    Image image = new Image();
                    image.setTenAnh((String) r.get("secure_url"));
                    System.out.println("Tên ảnh chính: " + image.getTenAnh());
                    listImage.add(image);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        List<Image> imageTotals = new ArrayList<>();
        for (Image i : listImage) {
            Image newImage = new Image();
            newImage.setSanPhamId(sanPham);
            newImage.setTenAnh(i.getTenAnh());
            newImage.setTrangThai(2);
            imageTotals.add(newImage);
        }
        imageRepository.saveAll(imageTotals);

        // Lưu ảnh theo màu
        String prefix = "images";
        List<Image> listImageByColor = new ArrayList<>();

        fileMap.entrySet().parallelStream().forEach(entry -> {
            String key = entry.getKey();
            List<MultipartFile> files = entry.getValue();

            files.forEach(file -> {
                if (!file.getName().equals("image")) {
                    BigInteger idMauSac = new BigInteger(file.getName().substring(prefix.length()));
                    try {
                        Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                        Image image = new Image();
                        image.setTenAnh((String) r.get("secure_url"));
                        image.setMauSacId(idMauSac);
                        listImageByColor.add(image);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        });

        List<Image> lstImageTotals = new ArrayList<>();
        for (Image i : listImageByColor) {
            Image newImage = new Image();
            newImage.setSanPhamId(sanPham);
            newImage.setTenAnh(i.getTenAnh());
            newImage.setTrangThai(1);
            newImage.setMauSacId(i.getMauSacId());
            lstImageTotals.add(newImage);
        }
        imageRepository.saveAll(lstImageTotals);

        dsMauSac.clear();
        dsRAM.clear();
        dsROM.clear();
        lstIM.clear();
        sanPham = new SanPham();
        lstCTSP.clear();

        this.success = "Thêm sản phẩm thành công";
        checkAlter = true;

        return "redirect:/san-pham/index";
    }

    // MÀN HÌNH THÊM MỚI - THÊM IM
    @PostMapping("/addIM/{idRAM}/{idROM}/{idMauSac}")
    public ResponseEntity<?> importIM(@PathVariable("idRAM") BigInteger idRAM,
                                      @PathVariable("idROM") BigInteger idROM,
                                      @PathVariable("idMauSac") BigInteger idMauSac,
                                      @RequestParam("inputField") String inputField) {

        List<String> inputList = Arrays.stream(inputField.split(","))
                .map(String::trim)
                .map(s -> s.replaceAll("\\s+", ""))
                .filter(s -> !s.isEmpty())
                .map(s -> "IM" + s)
                .collect(Collectors.toList());

        List<Imei> imeiToRemove = new ArrayList<>();
        List<Imei> listRepo = imeiRepository.findAll();
        for (int i = 0; i < lstCTSP.size(); i++) {
            ChiTietSanPham ctsp = lstCTSP.get(i);
            if (ctsp.getIdRAM().getId().equals(idRAM) &&
                    ctsp.getIdROM().getId().equals(idROM) &&
                    ctsp.getIdMauSac().getId().equals(idMauSac)) {

                List<String> imeiDTOS = lstIM.stream()
                        .flatMap(List::stream)
                        .collect(Collectors.toList());

                List<Imei> imeis = new ArrayList<>();
                for (String s : inputList) {
                    Imei imei = new Imei();
                    imei.setMaImei(s);
                    imei.setTrangThai(1);
                    imeis.add(imei);
                }

                for (int q = 0; q < imeis.size(); q++) {
                    for (int k = 0; k < listRepo.size(); k++) {
                        if (imeis.get(q).getMaImei().equals(listRepo.get(k).getMaImei())) {
                            imeiToRemove.add(imeis.get(q));
                        }
                    }
                    if (imeiDTOS.contains(imeis.get(q).getMaImei())) {
                        imeiToRemove.add(imeis.get(q));
                    }
                    if (imeis.contains(imeis.get(q).getMaImei())) {
                        imeiToRemove.add(imeis.get(q));
                    }
                }

                imeis.removeAll(imeiToRemove);

                for (int j = 0; j < imeis.size(); j++) {
                    lstIM.get(i).add(imeis.get(j).getMaImei());
                }
            }
        }

        if (imeiToRemove != null && !imeiToRemove.isEmpty()) {
            return ResponseEntity.ok("IM bị trùng: " + imeiToRemove.stream()
                    .map(Imei::getMaImei)
                    .collect(Collectors.joining(", ")));
        } else {
            return ResponseEntity.ok(null);
        }

    }

    //MÀN HÌNH THÊM MỚI - IMPORT IM
    @PostMapping("/importIM/{idRAM}/{idROM}/{idMauSac}")
    public ResponseEntity<?> importIM(@PathVariable("idRAM") BigInteger idRAM,
                                      @PathVariable("idROM") BigInteger idROM,
                                      @PathVariable("idMauSac") BigInteger idMauSac,
                                      @RequestParam("file") MultipartFile multipartFile) {

        List<Imei> imeiToRemove = new ArrayList<>();
        List<Imei> listRepo = imeiRepository.findAll();

        for (int i = 0; i < lstCTSP.size(); i++) {
            ChiTietSanPham ctsp = lstCTSP.get(i);
            if (ctsp.getIdRAM().getId().equals(idRAM) &&
                    ctsp.getIdROM().getId().equals(idROM) &&
                    ctsp.getIdMauSac().getId().equals(idMauSac)) {
                try (InputStream excelFile = multipartFile.getInputStream()) {
                    Workbook workbook = new XSSFWorkbook(excelFile);
                    Sheet datatypeSheet = workbook.getSheetAt(0);
                    DataFormatter fmt = new DataFormatter();
                    Iterator<Row> iterator = datatypeSheet.iterator();
                    Row firstRow = iterator.next();
                    Cell firstCell = firstRow.getCell(0);
                    List<Imei> listIMImport = new ArrayList<Imei>();

                    while (iterator.hasNext()) {
                        Row currentRow = iterator.next();
                        Imei nv = new Imei();
                        nv.setMaImei("IM" + currentRow.getCell(0).getStringCellValue());
                        nv.setTrangThai(1);
                        listIMImport.add(nv);
                    }
                    workbook.close();

                    List<String> imeiDTOS = lstIM.stream()
                            .flatMap(List::stream)
                            .collect(Collectors.toList());

                    for (int q = 0; q < listIMImport.size(); q++) {
                        for (int k = 0; k < listRepo.size(); k++) {
                            if (listIMImport.get(q).getMaImei().equals(listRepo.get(k).getMaImei())) {
                                imeiToRemove.add(listIMImport.get(q));
                            }
                        }
                        if (imeiDTOS.contains(listIMImport.get(q).getMaImei())) {
                            imeiToRemove.add(listIMImport.get(q));
                        }
                    }

                    listIMImport.removeAll(imeiToRemove);

                    for (int j = 0; j < listIMImport.size(); j++) {
                        lstIM.get(i).add(listIMImport.get(j).getMaImei());
                    }

                } catch (IOException | RuntimeException e) {
                    e.printStackTrace();
                    System.out.println("Có lỗi xảy ra: " + e.getMessage());
                }

            }

        }


        if (imeiToRemove != null && !imeiToRemove.isEmpty()) {
            return ResponseEntity.ok("IM bị trùng: " + imeiToRemove.stream()
                    .map(Imei::getMaImei)
                    .collect(Collectors.joining(", ")));
        } else {
            return ResponseEntity.ok(null);
        }
    }

    //MÀN HÌNH DANH SÁCH CHI TIẾT SẢN PHẨM
    @GetMapping("/detail/{id}")
    public String getCTSP(@PathVariable("id") BigInteger idSP,
                          @RequestParam("page") Optional<Integer> pageParam,
                          Model model, HttpSession session) {
        List<List<Imei>> lstImeiDetail = new ArrayList<>(new ArrayList<>());

        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 10);
        Page<ChiTietSanPham> lstCTSP = ctspRepository.findByIdSanPham(pageable, idSP);

        List<Integer> counts = new ArrayList<>();
        for (ChiTietSanPham chiTietSanPham : lstCTSP) {
            chiTietSanPham.setGiaBan(BigDecimal.valueOf(chiTietSanPham.getGiaBan().longValue()));
            counts.add(imeiRepository.countIM(chiTietSanPham.getId()));
        }

        model.addAttribute("lstRom", romRepository.findByTrangThai(ROMRepository.ACTIVE));
        model.addAttribute("lstRam", ramRepository.findByTrangThai(RAMRepository.ACTIVE));
        model.addAttribute("lstMauSac", mauSacRepository.findByTrangThai(MauSacRepository.ACTIVE));
        model.addAttribute("lstCTSP", lstCTSP);

        List<Map<String, Object>> list = imeiRepository.findAllByIdSanPhamAndStatus(idSP);
        if(list!= null){
            System.out.println(list);

        }
        model.addAttribute("listIM", list);

        model.addAttribute("lstSL", counts);
        model.addAttribute("idSP", idSP);

        session.removeAttribute("idSP");
        session.setAttribute("idSP", idSP);

        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Chi tiết sản phẩm");

        if (checkAlter == true) {
            model.addAttribute("success", this.success);
            model.addAttribute("error", this.error);
            this.success = null;
            checkAlter = false;
        }

        return "admin/ql_chi_tiet_sp/detail";

    }

    //MÀN HÌNH CHI TIẾT SẢN PHẨM MỞ RỘNG
    @GetMapping("/chi-tiet-san-pham/detail/{id}")
    public String getAllIMByIdSPCT(@PathVariable("id") BigInteger id,
                                   @RequestParam("page") Optional<Integer> pageParam,
                                   Model model) {
        if (checkAlter == true) {
            model.addAttribute("success", this.success);
        }
        int page = pageParam.orElse(0);
        Pageable p = PageRequest.of(page, 5);
        Page<Imei> list = imeiRepository.findAllByIdChiTietSanPham(id, p);
        Optional<ChiTietSanPham> optionalSanPham = chiTietSanPhamRepository.findById(id);

        model.addAttribute("idSPCT", id);
        model.addAttribute("idSP",optionalSanPham.get().getIdSanPham().getId());
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách imei sản phẩm");
        model.addAttribute("listIM", list);

        if (checkAlter == true) {
            model.addAttribute("success", this.success);
            model.addAttribute("error", this.error);
            this.success = null;
            checkAlter = false;
        }

        return "admin/ql_chi_tiet_sp/spct_detail";
    }


    //MÀN HÌNH CHI TIẾT SẢN PHẨM MỞ RỘNG - IMPORT IM
    @PostMapping("/import-spct/{id}")
    public String importSPC(@PathVariable("id") BigInteger id,
                            @RequestParam("file") MultipartFile multipartFile,
                            HttpSession session,
                            Model model) {

        BigInteger idSP = (BigInteger) session.getAttribute("idSP");
        Optional<ChiTietSanPham> optionalChiTietSanPham = ctspRepository.findById(id);
        List<Imei> imeiToRemove = new ArrayList<>();

        try (InputStream excelFile = multipartFile.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            // Bỏ qua hàng đầu tiên (tiêu đề)
            if (iterator.hasNext()) {
                iterator.next();
            }

            List<Imei> listOfNhanVien = new ArrayList<Imei>();

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                Imei nv = new Imei();
                nv.setMaImei(currentRow.getCell(0).getStringCellValue());
                nv.setTrangThai(1);
                listOfNhanVien.add(nv);
            }
            workbook.close();

            listOfNhanVien.forEach(x -> x.setMaImei("IM" + x.getMaImei()));

            listOfNhanVien.stream()
                    .collect(Collectors.groupingBy(Imei::getMaImei))
                    .values().stream()
                    .filter(ims -> ims.size() > 1)
                    .forEach(ims -> {
                        imeiToRemove.addAll(ims);
                        listOfNhanVien.removeAll(ims);
                    });

            List<Imei> list = imeiRepository.findAll();
            for (Imei imei : listOfNhanVien) {
                for (Imei existingImei : list) {
                    if (imei.getMaImei().equals(existingImei.getMaImei())) {
                        imeiToRemove.add(imei);
                    }
                }
            }
            listOfNhanVien.removeAll(imeiToRemove);

            listOfNhanVien.forEach(x -> x.setIdChiTietSP(optionalChiTietSanPham.get()));
            imeiRepository.saveAll(listOfNhanVien);

        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            System.out.println("Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/san-pham/chi-tiet-san-pham/detail/" + id;
        }


        if (imeiToRemove.size() > 0) {
            this.success = "IM bị trùng" + imeiToRemove.stream().map(x -> x.getMaImei()).collect(Collectors.toList());
        } else {
            this.success = "Thêm IM thành công";
        }
        checkAlter = true;


        return "redirect:/san-pham/chi-tiet-san-pham/detail/" + id;
    }

    //MÀN HÌNH CHI TIẾT SẢN PHẨM MỞ RỘNG - THÊM IM
    @PostMapping("/chi-tiet-san-pham/addIM/{id}")
    public String updateIM(@PathVariable("id") BigInteger id,
                           @RequestParam("inputField") String inputField, Model model) {
        List<String> inputList = Arrays.stream(inputField.split(","))
                .map(String::trim)
                .map(s -> s.replaceAll("\\s+", ""))
                .filter(s -> !s.isEmpty())
                .map(s -> "IM" + s)
                .collect(Collectors.toList());

        Optional<ChiTietSanPham> optionalChiTietSanPham = ctspRepository.findById(id);

        List<String> imeiToRemove = new ArrayList<>();

        Set<String> uniqueSet = new HashSet<>();

        for (String item : inputList) {
            if (!uniqueSet.add(item)) {
                imeiToRemove.add(item);
            }
        }
        inputList.removeAll(imeiToRemove);

        List<Imei> list = imeiRepository.findAll();
        for (int i = 0; i < inputList.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (inputList.get(i).equals(list.get(j).getMaImei())) {
                    imeiToRemove.add(inputList.get(i));
                }
            }
        }

        List<Imei> imeis = new ArrayList<>();
        inputList.removeAll(imeiToRemove);
        for (String s : inputList) {
            Imei imei = new Imei();
            imei.setIdChiTietSP(optionalChiTietSanPham.get());
            imei.setMaImei(s);
            imei.setTrangThai(1);
            imeis.add(imei);
        }

        imeiRepository.saveAll(imeis);

        if (imeiToRemove.size() > 0) {
            this.success = "IM bị trùng" + imeiToRemove;
        } else {
            this.success = "Thêm IM thành công";
        }
        checkAlter = true;

        return "redirect:/san-pham/chi-tiet-san-pham/detail/" + id;

    }

    @GetMapping("/chi-tiet-san-pham/restore/{id}")
    public String restoreCTSP(@PathVariable("id") BigInteger id, HttpSession session, Model model) {
        Optional<ChiTietSanPham> optionalChiTietSanPham = ctspRepository.findById(id);
        if (optionalChiTietSanPham.isPresent()) {
            optionalChiTietSanPham.get().setTrangThai(1);
            ctspRepository.save(optionalChiTietSanPham.get());

            this.success = "Khôi phục sản phẩm thành công";
            checkAlter = true;
        }
        BigInteger url = (BigInteger) session.getAttribute("idSP");
        return "redirect:/san-pham/detail/" + url;
    }

    @GetMapping("/chi-tiet-san-pham/delete/{id}")
    public String deleteCTSP(@PathVariable("id") BigInteger id, HttpSession session, Model model) {
        Optional<ChiTietSanPham> optionalChiTietSanPham = ctspRepository.findById(id);
        if (optionalChiTietSanPham.isPresent()) {
            optionalChiTietSanPham.get().setTrangThai(0);
            ctspRepository.save(optionalChiTietSanPham.get());

            this.success = "Đóng sản phẩm thành công";
            checkAlter = true;
        }
        BigInteger url = (BigInteger) session.getAttribute("idSP");
        return "redirect:/san-pham/detail/" + url;
    }

    //MÀN HÌNH DANH SÁCH CHI TIẾT SẢN PHẨM  - CẬP NHẬT GIÁ
    @PostMapping("/chi-tiet-san-pham/update-price/{id}")
    public String updatePriceCTSP(@PathVariable("id") BigInteger id, @RequestParam("price") BigDecimal price, HttpSession session, Model model) {
        Optional<ChiTietSanPham> optionalChiTietSanPham = ctspRepository.findById(id);
        if (optionalChiTietSanPham.isPresent()) {
            optionalChiTietSanPham.get().setGiaBan(price);
            ctspRepository.save(optionalChiTietSanPham.get());
            this.success = "Cập nhật sản phẩm thành công";
            checkAlter = true;

        }
        BigInteger url = (BigInteger) session.getAttribute("idSP");
        return "redirect:/san-pham/detail/" + url;
    }

    //MÀN HÌNH DANH SÁCH CHI TIẾT SẢN PHẨM  - CẬP NHẬT GIÁ HÀNG LOẠT
    @PostMapping("/chi-tiet-san-pham/update-bulk-price")
    public String updatePrice2CTSP(@RequestBody Map<String, Object> payload, HttpSession session
    ) {
        List<String> ids = Collections.singletonList(MapUtils.getString(payload, "ids"));
        BigDecimal price = new BigDecimal(MapUtils.getString(payload, "price"));
        List<BigInteger> integerIds = new ArrayList<>();
        for (String id : ids) {
            String[] idArray = id.replaceAll("[\\[\\]]", "").split(", ");
            for (String num : idArray) {
                integerIds.add(BigInteger.valueOf(Long.parseLong(num)));
            }
        }
        List<ChiTietSanPham> chiTietSanPhams = ctspRepository.findAllById(integerIds);
        for (ChiTietSanPham chiTietSanPham : chiTietSanPhams
        ) {
            chiTietSanPham.setGiaBan(price);
        }

        ctspRepository.saveAll(chiTietSanPhams);
        this.success = "Cập nhật sản phẩm thành công";
        checkAlter = true;

        BigInteger url = (BigInteger) session.getAttribute("idSP");
        return "redirect:/san-pham/detail/" + url;
    }

    //MÀN HÌNH SẢN PHẨM  -  ĐÓNG
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") BigInteger id, Model model) {
        Optional<SanPham> optionalSP = sanPhamRepository.findById(id);
        if (optionalSP.isPresent()) {
            optionalSP.get().setTrangThai(0);
            sanPhamRepository.save(optionalSP.get());
        }

        this.success = "Xoá sản phẩm thành công";
        checkAlter = true;

        return "redirect:/san-pham/index";
    }

    //MÀN HÌNH SẢN PHẨM  -  KHÔI PHỤC
    @GetMapping("/restore/{id}")
    public String restore(@PathVariable("id") BigInteger id, Model model) {
        Optional<SanPham> optionalSP = sanPhamRepository.findById(id);
        if (optionalSP.isPresent()) {
            optionalSP.get().setTrangThai(1);
            sanPhamRepository.save(optionalSP.get());
        }

        this.success = "Khôi phục sản phẩm thành công";
        checkAlter = true;

        return "redirect:/san-pham/index";
    }

    //MÀN HÌNH SẢN PHẨM - TÌM KIẾM
    @RequestMapping("/search")
    public String searchProduct(@RequestParam("page") Optional<Integer> pageRequest,
                                @RequestParam(defaultValue = "", name = "search") String search,
                                @RequestParam(defaultValue = "", name = "idHeDieuHanh") BigInteger idHeDieuHanh,
                                @RequestParam(defaultValue = "", name = "idHang") BigInteger idHang,
                                @RequestParam(defaultValue = "", name = "idManHinh") BigInteger idManHinh,
                                @RequestParam(defaultValue = "", name = "status") Integer status,
                                Model model
    ) {
        int page = pageRequest.orElse(0);
        Pageable pageable = PageRequest.of(page, 10);
        Page<SanPham> data = sanPhamRepository.search(idHang, idHeDieuHanh, idManHinh, search, status, pageable);
        List<Pin> pins = pinRepository.findAllBy(1);
        model.addAttribute("lstPin", pins);
        model.addAttribute("lstManHinh", manHinhRepository.findByTrangThai(ManHinhRepository.ACTIVE));
        model.addAttribute("lstHeDieuHanh", heDieuHanhRepository.findByTrangThai(HeDieuHanhRepository.ACTIVE));
        model.addAttribute("lstHang", hangRepository.findByTrangThai(HangRepository.ACTIVE));
        model.addAttribute("data", data);
        model.addAttribute("checkSearch", true);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("idHang", idHang);
        model.addAttribute("idHeDieuHanh", idHeDieuHanh);
        model.addAttribute("idManHinh", idManHinh);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách sản phẩm");

        if (checkAlter == true) {
            model.addAttribute("success", this.success);
            this.success = null;
            checkAlter = false;
        }
        return "admin/ql_chi_tiet_sp/index";
    }

    //MÀN HÌNH DANH SÁCH CHI TIẾT SẢN PHẨM - TÌM KIẾM
    @RequestMapping("/chi-tiet-san-pham/search")
    public String searchChiTietSanPham(@RequestParam("page") Optional<Integer> pageRequest,
                                       @RequestParam(defaultValue = "", name = "search") String search,
                                       @RequestParam(defaultValue = "", name = "idMauSac") BigInteger idMauSac,
                                       @RequestParam(defaultValue = "", name = "idRom") BigInteger idRom,
                                       @RequestParam(defaultValue = "", name = "idRam") BigInteger idRam,
                                       @RequestParam(defaultValue = "", name = "status") Integer status,
                                       HttpSession session, Model model
    ) {
        BigInteger idSP = (BigInteger) session.getAttribute("idSP");
        int page = pageRequest.orElse(0);
        Pageable pageable = PageRequest.of(page, 10);
        Page<ChiTietSanPham> lstCTSP = ctspRepository.searchChiTietSanPham(idSP,idMauSac, idRom, idRam, search, status, pageable);
        List<Integer> counts = new ArrayList<>();
        for (ChiTietSanPham chiTietSanPham : lstCTSP) {
            counts.add(imeiRepository.countIM(chiTietSanPham.getId()));
        }
        List<Map<String, Object>> list = imeiRepository.findAllByIdSanPhamAndStatus(idSP);
        model.addAttribute("lstSL", counts);
        model.addAttribute("listIM", list);
        model.addAttribute("lstCTSP", lstCTSP);
        model.addAttribute("checkSearch", true);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("idMauSac", idMauSac);
        model.addAttribute("idRom", idRom);
        model.addAttribute("idRam", idRam);
        model.addAttribute("lstRom", romRepository.findByTrangThai(ROMRepository.ACTIVE));
        model.addAttribute("lstRam", ramRepository.findByTrangThai(RAMRepository.ACTIVE));
        model.addAttribute("lstMauSac", mauSacRepository.findByTrangThai(MauSacRepository.ACTIVE));
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách sản phẩm");

        if (checkAlter == true) {
            model.addAttribute("success", this.success);
            this.success = null;
            checkAlter = false;
        }

        return "admin/ql_chi_tiet_sp/detail";
    }


    //MÀN HÌNH THÊM MỚI - HUỶ
    @GetMapping("them/huy")
    public String huy() {
        lstCTSP.clear();
        dsMauSac.clear();
        dsRAM.clear();
        dsROM.clear();
        lstIM.clear();
        sanPham = new SanPham();
        return "redirect:/san-pham/create-product";

    }

    @GetMapping("them-moi")
    public String themMoi() {
        lstCTSP.clear();
        dsMauSac.clear();
        dsRAM.clear();
        dsROM.clear();
        lstIM.clear();
        sanPham = new SanPham();
        return "redirect:/san-pham/create-product";

    }

    public void deleteImageByPublicId(String url) throws IOException {
        String publicId = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.'));
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }
}
