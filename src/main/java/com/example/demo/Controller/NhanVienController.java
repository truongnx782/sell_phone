//package com.example.demo.Controller;
//
//<<<<<<< HEAD
//import java.io.InputStream;
//import java.nio.file.CopyOption;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.Date;
//import java.util.Optional;
//
//import com.example.demo.Entitys.ChucVu;
//import com.example.demo.Entitys.NhanVien;
//import com.example.demo.Repository.ChucVuRepository;
//import com.example.demo.Repository.NhanVienRepository;
//=======
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import com.example.demo.Entitys.NhanVien;
//import com.example.demo.Repository.DiaChiRepository;
//import com.example.demo.Repository.NhanVienRepository;
//import com.example.demo.Request.NhanVienRequest;
//import com.example.demo.Utils.RepoUtuils;
//import com.google.zxing.*;
//import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
//import com.google.zxing.common.HybridBinarizer;
//import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.xssf.usermodel.XSSFCell;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;
//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.domain.Sort.Direction;
//<<<<<<< HEAD
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.util.StringUtils;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.multipart.MultipartFile;
//
//@Controller
//@RequestMapping({"nhan-vien"})
//public class NhanVienController {
//    @Autowired
//    private NhanVienRepository nvRepo;
//    @Autowired
//    private ChucVuRepository cvRepo;
//    String success;
//    String error;
//
//    public NhanVienController() {
//    }
//
//    @GetMapping({"hien-thi"})
//    public String hienThi(Model model, @RequestParam("page") Optional<Integer> pageParam) {
//        int page = (Integer)pageParam.orElse(0);
//=======
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.text.Normalizer;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDate;
//import java.time.ZoneId;
//import java.util.*;
//
//
//@Controller
//@RequestMapping({"nhan-vien"})
//public class NhanVienController extends RepoUtuils {
//    private final NhanVienRepository nvRepo;
//    private final DiaChiRepository diaChiRepository;
//
//    public NhanVienController(NhanVienRepository nvRepo, DiaChiRepository diaChiRepository) {
//        this.nvRepo = nvRepo;
//        this.diaChiRepository = diaChiRepository;
//    }
//
//    @Autowired
//    JavaMailSender javaMailSender;
//    @Autowired
//    Cloudinary cloudinary;
//
//    String success;
//    String error;
//
//
//    @GetMapping("/route")
//    public String yourHandlerMethod() {
//        return "redirect:/nhan-vien/hien-thi";
//    }
//
//    @GetMapping({"hien-thi"})
//    public String hienThi(Model model, @RequestParam("page") Optional<Integer> pageParam, HttpSession session) {
//        int page = (Integer) pageParam.orElse(0);
//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22
//        Pageable p = PageRequest.of(page, 5, Sort.by(Direction.DESC, new String[]{"id"}));
//        Page<NhanVien> listNV = this.nvRepo.findAll(p);
//        model.addAttribute("listNV", listNV);
//        model.addAttribute("success", this.success);
//        model.addAttribute("error", this.error);
//<<<<<<< HEAD
//=======
//        model.addAttribute("message_title1", "Quản lý nhân viên");
//        model.addAttribute("message_title2", "Danh sách nhân viên");
//        session.removeAttribute("SSnhanVien");
//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22
//        this.success = null;
//        return "nhan-vien/index";
//    }
//
//<<<<<<< HEAD
//    @PostMapping({"search"})
//    public String search(@RequestParam("search") String search, @RequestParam("page") Optional<Integer> pageParam, Model model) {
//        int page = (Integer)pageParam.orElse(0);
//        Pageable p = PageRequest.of(page, 5);
//        Page<NhanVien> listSearch = this.nvRepo.findAllByMaNhanVienOrTenNhanVienContainingOrSdtContaining(search, search, search, p);
//        model.addAttribute("listNV", listSearch);
//        return "nhan-vien/index";
//    }
//
//    @GetMapping({"view-add"})
//    public String viewAdd(Model model) {
//        NhanVien nv = new NhanVien();
//        nv.setIdChucVu((ChucVu) this.cvRepo.findById(2).get());
//=======
//    @GetMapping({"view-add"})
//    public String viewAdd(Model model, HttpSession session) {
//        NhanVien nhanVien = (NhanVien) session.getAttribute("SSnhanVien");
//        NhanVien nv = new NhanVien(); // Tạo một đối tượng mới
//        if (nhanVien != null) {
//            nv.setTenNhanVien(nhanVien.getTenNhanVien());
//            nv.setGioiTinh(nhanVien.getGioiTinh());
//            nv.setCCCD(nhanVien.getCCCD());
//            nv.setNgaySinh(nhanVien.getNgaySinh());
//        }
//        nv.setIdChucVu(2);
//        model.addAttribute("message_title1", "Quản lý tài khoản");
//        model.addAttribute("message_title2", "Thêm nhân viên");
//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22
//        model.addAttribute("nhanVien", nv);
//        return "nhan-vien/add";
//    }
//
//<<<<<<< HEAD
//    @PostMapping({"add"})
//    public String add(@ModelAttribute("nhanVien") NhanVien nv, Model model, @RequestParam("avatar") MultipartFile file) {
//        try {
//            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//            String uploadDir = "/assets/img/avatars";
//            Path uploadPath = Paths.get(uploadDir);
//            Files.createDirectories(uploadPath);
//            Path filePath = uploadPath.resolve(fileName);
//            InputStream inputStream = file.getInputStream();
//
//            try {
//                Files.copy(inputStream, filePath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
//                nv.setAnhNhanVien(filePath.toString());
//            } catch (Throwable var13) {
//                if (inputStream != null) {
//                    try {
//                        inputStream.close();
//                    } catch (Throwable var12) {
//                        var13.addSuppressed(var12);
//                    }
//                }
//
//                throw var13;
//            }
//
//            if (inputStream != null) {
//                inputStream.close();
//            }
//        } catch (Exception var14) {
//            var14.printStackTrace();
//        }
//
//        try {
//            int var10001 = this.nvRepo.findAll().size();
//            nv.setMaNhanVien("NV" + (var10001 + 1));
//            nv.setTrangThai(1);
//            nv.setNgayTao(new Date());
//            this.nvRepo.save(nv);
//            this.success = "Thêm nhân viên thành công";
//        } catch (Exception var11) {
//            this.success = null;
//            var11.printStackTrace();
//        }
//
//        return "redirect:/nhan-vien/hien-thi";
//    }
//
//=======
//    public static String removeAccents(String input) {
//        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
//    }
//
//    @PostMapping("/add")
//    public String add(@Valid @ModelAttribute("nhanVien") NhanVienRequest nhanVienRequest,
//                      BindingResult result, HttpSession session,
//                      Model model, @RequestParam("avatar") MultipartFile file) throws IOException {
//        String image = "/assets/img/avatars/logo.png";
//        if (result.hasErrors()) {
//            System.out.println("co loi");
//            model.addAttribute("message_title1", "quản lý khách hàng");
//            model.addAttribute("message_title2", "Cập nhật khách hàng");
//            return "nhan-vien/add";
//        }
//        if (!file.isEmpty()) {
//            Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
//            image = (String) r.get("secure_url");
//        }
//        try {
//            String chuoi = nhanVienRequest.getTenNhanVien();
//            String[] cacTu = chuoi.split("\\s+");
//            StringBuilder ketQua = new StringBuilder();
//            String truong = removeAccents(cacTu[cacTu.length - 1]);
//            ketQua.append(truong);
//            for (int i = 0; i < cacTu.length - 1; i++) {
//                if (cacTu[i].length() > 0) {
//                    ketQua.append(cacTu[i].charAt(0));
//                }
//            }
//
//            NhanVien nv = new NhanVien();
//            System.out.println(ketQua.toString());
//            Optional<NhanVien> maxIdNhanVien = nvRepo.findMaId();
//            int maxId = maxIdNhanVien.isPresent() ? maxIdNhanVien.get().getId() + 1 : 0;
//            System.out.println(maxId);
//            nv.setMaNhanVien(ketQua.toString().toLowerCase(Locale.ROOT) + "NV" + maxId);
//            nv.setTenNhanVien(nhanVienRequest.getTenNhanVien());
//            nv.setSdt(nhanVienRequest.getSdt());
//            nv.setEmail(nhanVienRequest.getEmail());
//            nv.setCCCD(nhanVienRequest.getCCCD());
//            nv.setNgaySinh(nhanVienRequest.getNgaySinh());
//            nv.setDiaChiCuThe(nhanVienRequest.getDiaChiCuThe());
//            nv.setTinh(nhanVienRequest.getTinh());
//            nv.setQuan(nhanVienRequest.getQuan());
//            nv.setPhuong(nhanVienRequest.getPhuong());
//            nv.setGhiChu(nhanVienRequest.getGhiChu());
//            nv.setGioiTinh(nhanVienRequest.getGioiTinh());
//            nv.setTrangThai(1);
//            nv.setIdChucVu(1);
//            nv.setNgayTao(new Date());
//            nv.setAnhNhanVien(image);
//
//            Random random = new Random();
//            int randomNumber = random.nextInt(9000) + 1000;
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo(nv.getEmail());
//            msg.setSubject("CREATE ACCOUNT");
//            msg.setText("Username: " + nv.getEmail() + "/ Password:" + randomNumber);
//            javaMailSender.send(msg);
//            nv.setMatKhau(String.valueOf(randomNumber));
//            this.nvRepo.save(nv);
//            this.success = "Thêm nhân viên thành công";
//            session.removeAttribute("SSnhanVien");
//        } catch (Exception e) {
//            this.success = null;
//            e.printStackTrace();
//            return "redirect:/error";
//        }
//        return "redirect:/nhan-vien/hien-thi";
//    }
//
//
//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22
//    @GetMapping({"view-update/{id}"})
//    public String viewUpdate(Model model, @PathVariable("id") NhanVien nv) {
//        model.addAttribute("nhanVien", nv);
//        model.addAttribute("maNV", "MÃ NHÂN VIÊN: " + nv.getMaNhanVien());
//<<<<<<< HEAD
//        return "nhan-vien/update";
//    }
//
//    @PostMapping({"update/{id}"})
//    public String update(@ModelAttribute("nhanVien") NhanVien nv, Model model, @RequestParam("avatar") MultipartFile file) {
//        NhanVien nhanVien = (NhanVien)this.nvRepo.findById(nv.getId()).get();
//        nv.setMaNhanVien(nhanVien.getMaNhanVien());
//        nv.setNgaySua(new Date());
//        nv.setTrangThai(1);
//
//        try {
//            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
//            String uploadDir = "/assets/img/avatars";
//            Path uploadPath = Paths.get(uploadDir);
//            Files.createDirectories(uploadPath);
//            Path filePath = uploadPath.resolve(fileName);
//            InputStream inputStream = file.getInputStream();
//
//            try {
//                Files.copy(inputStream, filePath, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
//                nv.setAnhNhanVien(filePath.toString());
//            } catch (Throwable var14) {
//                if (inputStream != null) {
//                    try {
//                        inputStream.close();
//                    } catch (Throwable var13) {
//                        var14.addSuppressed(var13);
//                    }
//                }
//
//                throw var14;
//            }
//
//            if (inputStream != null) {
//                inputStream.close();
//            }
//        } catch (Exception var15) {
//            var15.printStackTrace();
//        }
//
//        try {
//            this.nvRepo.save(nv);
//            this.success = "Cập nhật nhân viên thành công";
//        } catch (Exception var12) {
//            this.success = null;
//        }
//
//=======
//        model.addAttribute("message_title1", "Quản lý tài khoản");
//        model.addAttribute("message_title2", "sửa thông tin nhân viên");
//        return "nhan-vien/update";
//    }
//
//    @PostMapping("/update/{id}")
//    public String update(@Valid @ModelAttribute("nhanVien") NhanVienRequest nhanVienRequest,
//                         BindingResult result, Model model,
//                         @PathVariable("id") Integer idNV,
//                         @RequestParam("avatar") MultipartFile file) throws IOException {
//        if (result.hasErrors()) {
//            System.out.println("co loi");
//            model.addAttribute("message_title1", "quản lý khách hàng");
//            model.addAttribute("message_title2", "Cập nhật khách hàng");
//            return "nhan-vien/update";
//        }
//
//        Optional<NhanVien> optionalNhanVien = nvRepo.findById(idNV);
//        if (!optionalNhanVien.isPresent()) {
//            return "redirect:/error";
//        }
//        String image = "/assets/img/avatars/logo.png";
//        NhanVien nhanVien = optionalNhanVien.get();
//        nhanVien.setTenNhanVien(nhanVienRequest.getTenNhanVien());
//        nhanVien.setSdt(nhanVienRequest.getSdt());
//        nhanVien.setCCCD(nhanVienRequest.getCCCD());
//        nhanVien.setEmail(nhanVienRequest.getEmail());
//        nhanVien.setNgaySinh(nhanVienRequest.getNgaySinh());
//        nhanVien.setTinh(nhanVienRequest.getTinh());
//        nhanVien.setQuan(nhanVienRequest.getQuan());
//        nhanVien.setPhuong(nhanVienRequest.getPhuong());
//        nhanVien.setDiaChiCuThe(nhanVienRequest.getDiaChiCuThe());
//        nhanVien.setGhiChu(nhanVienRequest.getGhiChu());
//        nhanVien.setGioiTinh(nhanVienRequest.getGioiTinh());
//        nhanVien.setNgaySua(new Date());
//        if (!file.isEmpty()) {
//            Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
//            image = (String) r.get("secure_url");
//        }
//        nhanVien.setAnhNhanVien(image);
//        try {
//            this.nvRepo.save(nhanVien);
//            this.success = "Cập nhật nhân viên thành công";
//        } catch (Exception e) {
//            this.success = null;
//            e.printStackTrace();
//            return "redirect:/error";
//        }
//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22
//        return "redirect:/nhan-vien/hien-thi";
//    }
//
//    @GetMapping({"delete/{id}"})
//    public String delete(@PathVariable("id") NhanVien nv) {
//<<<<<<< HEAD
//        nv.setTrangThai(0);
//        this.nvRepo.save(nv);
//        return "redirect:/nhan-vien/hien-thi";
//    }
//}
//=======
//        System.out.println(nv.getTenNhanVien());
//        nv.setTrangThai(0);
//        this.nvRepo.save(nv);
//        this.success = "Xoá nhân viên thành công";
//        return "redirect:/nhan-vien/hien-thi";
//    }
//
//    @RequestMapping("/search")
//    public String search(@RequestParam("page") Optional<Integer> pageParam,
//                         @RequestParam(defaultValue = "", name = "search") String search,
//                         @RequestParam(defaultValue = "", name = "status") Integer status,
//                         Model model) {
//        int page = pageParam.orElse(0);
//        Pageable p = PageRequest.of(page, 5);
//        Page<NhanVien> nhanVienPage = nvRepo.findAllBySearch(search, status, p);
//        NhanVien nv = new NhanVien();
//        model.addAttribute("checkSearch", true);
//        model.addAttribute("listNV", nhanVienPage);
//        model.addAttribute("nhanVien", nv);
//        model.addAttribute("message_title1", "quản lý nhân viên");
//        model.addAttribute("message_title2", "Danh sách nhân viên");
//        model.addAttribute("search", search);
//        model.addAttribute("status", status);
//        return "nhan-vien/index";
//    }
//
//    @PostMapping("/import")
//    public String importExcel(@RequestPart("file") MultipartFile multipartFile, HttpSession session) {
//        if (multipartFile.isEmpty()) {
//            return "redirect:/nhan-vien/hien-thi";
//        }
//        try (InputStream excelFile = multipartFile.getInputStream()) {
//            Workbook workbook = new XSSFWorkbook(excelFile);
//            Sheet datatypeSheet = workbook.getSheetAt(0);
//            DataFormatter fmt = new DataFormatter();
//            Iterator<Row> iterator = datatypeSheet.iterator();
//            Row firstRow = iterator.next();
//            Cell firstCell = firstRow.getCell(0);
//            System.out.println(firstCell.getStringCellValue());
//            List<NhanVien> listOfNhanVien = new ArrayList<NhanVien>();
//            int demMa = 0;
//            int maId = 1; // Tạo biến để lưu trữ mã mới cho mỗi nhân viên
//            Optional<NhanVien> maxIdNhanVien = nvRepo.findMaId();
//            if (maxIdNhanVien.isPresent()) {
//                maId = maxIdNhanVien.get().getId() + 1;
//            }
//
//            while (iterator.hasNext()) {
//                Row currentRow = iterator.next();
//                NhanVien nv = new NhanVien();
//                String chuoi = currentRow.getCell(0).getStringCellValue();
//                String[] cacTu = chuoi.split("\\s+");
//                StringBuilder ketQua = new StringBuilder();
//                String truong = removeAccents(cacTu[cacTu.length - 1]);
//                ketQua.append(truong);
//                for (int i = 0; i < cacTu.length - 1; i++) {
//                    if (cacTu[i].length() > 0) {
//                        ketQua.append(cacTu[i].charAt(0));
//                    }
//                }
//                nv.setMaNhanVien(ketQua.toString().toLowerCase(Locale.ROOT) + "NV" + (maId + demMa));
//                nv.setTenNhanVien(currentRow.getCell(0).getStringCellValue());
//                nv.setSdt(currentRow.getCell(1).toString());
//                nv.setEmail(currentRow.getCell(2).getStringCellValue());
//                nv.setCCCD(currentRow.getCell(3).getStringCellValue());
//                nv.setDiaChiCuThe(currentRow.getCell(4).getStringCellValue());
//                nv.setPhuong(currentRow.getCell(5).getStringCellValue());
//                nv.setQuan(currentRow.getCell(6).getStringCellValue());
//                nv.setTinh(currentRow.getCell(7).getStringCellValue());
//
//                Random random = new Random();
//                int randomNumber = random.nextInt(9000) + 1000;
//                SimpleMailMessage msg = new SimpleMailMessage();
//                msg.setTo(nv.getEmail());
//                msg.setSubject("CREATE ACCOUNT");
//                msg.setText("Username: " + nv.getEmail() + "/ Password:" + randomNumber);
//                javaMailSender.send(msg);
//                nv.setMatKhau(String.valueOf(randomNumber));
//                nv.setTrangThai(1);
//                XSSFCell cell = (XSSFCell) currentRow.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
//                Date ngaySinh = null;
//                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
//                    ngaySinh = cell.getDateCellValue();
//                } else if (cell.getCellTypeEnum() == CellType.STRING) {
//                    try {
//                        LocalDate localDate = LocalDate.parse(cell.getStringCellValue());
//                        ngaySinh = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//                nv.setNgaySinh(ngaySinh);
//                nv.setGioiTinh(currentRow.getCell(9).getStringCellValue().equals("Nam") ? true : false);
//                nv.setNgayTao(new Date());
//                nv.setIdChucVu(STAFF);
//                nv.setTrangThai(ACTIVE);
//                demMa++;
//                listOfNhanVien.add(nv);
//            }
//            workbook.close();
//
//            List<NhanVien> listkhR = nvRepo.findAll();
//            List<NhanVien> staffToRemove = new ArrayList<>();
//            for (int i = 0; i < listkhR.size(); i++) {
//                for (int j = 0; j < listOfNhanVien.size(); j++) {
//                    if (listkhR.get(i).getSdt().equals(listOfNhanVien.get(j).getSdt())
//                            || listkhR.get(i).getCCCD().equals(listOfNhanVien.get(j).getCCCD())
//                            || listkhR.get(i).getEmail().equals(listOfNhanVien.get(j).getEmail())
//                    ) {
//                        staffToRemove.add(listOfNhanVien.get(j));
//                    }
//                }
//            }
//            listOfNhanVien.removeAll(staffToRemove);
//            nvRepo.saveAll(listOfNhanVien);
//        } catch (IOException | RuntimeException e) {
//            e.printStackTrace();
//            System.out.println("Có lỗi xảy ra: " + e.getMessage());
//            return "redirect:/nhan-vien/hien-thi";
//        }
//        return "redirect:/nhan-vien/hien-thi";
//
//    }
//
//
//        @PostMapping("/scanQRCode")
//        public String scanQRCode (@RequestParam(value = "file", required = false) MultipartFile file, Model
//        model, HttpSession session){
//            try {
//                String qrCodeText;
//                if (file != null) {
//                    qrCodeText = decodeQRCode(file.getBytes());
//                } else {
//                    // Không có file, thử quét từ webcam
//                    qrCodeText = "This is a placeholder for webcam scanning.";
//                }
//                System.out.println(qrCodeText);
//                String[] qrCodeValues = qrCodeText.split("\\|");
//                String code = qrCodeValues[0];
//                String birthday = qrCodeValues[1];
//                String name = qrCodeValues[2];
//                String birthdate = qrCodeValues[3];
//                String gender = qrCodeValues[4];
//                String address = qrCodeValues[5];
//                String issueDate = qrCodeValues[6];
//                System.out.println("Code: " + code);
//                System.out.println("Birthday: " + birthday);
//                System.out.println("Name: " + name);
//                System.out.println("Birthdate: " + birthdate);
//                System.out.println("Gender: " + gender);
//                System.out.println("Address: " + address);
//                System.out.println("Issue Date: " + issueDate);
//                NhanVien nhanVien = new NhanVien();
//                nhanVien.setTenNhanVien(name);
//                nhanVien.setCCCD(code);
//                if (gender.equals("Nam")) {
//                    nhanVien.setGioiTinh(true);
//                }
//                if (gender.equals("Nữ")) {
//                    nhanVien.setGioiTinh(false);
//                }
//                SimpleDateFormat dateFormatInput = new SimpleDateFormat("ddMMyyyy");
//                SimpleDateFormat dateFormatOutput = new SimpleDateFormat("dd/MM/yyyy");
//                try {
//                    Date ngaySinh = dateFormatInput.parse(birthdate); // Chuyển đổi từ ddMMyyyy sang Date
//                    String ngaySinhFormatted = dateFormatOutput.format(ngaySinh); // Chuyển đổi lại sang định dạng dd/MM/yyyy
//                    System.out.println("Ngày sinh: " + ngaySinhFormatted);
//                    nhanVien.setNgaySinh(ngaySinh);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                session.setAttribute("SSnhanVien", nhanVien);
//                return "redirect:/nhan-vien/view-add";
//            } catch (IOException | NotFoundException e) {
//                model.addAttribute("error", "QR code not found or couldn't be decoded.");
//            }
//            return "scan/qr_result"; // Trả về trang HTML để hiển thị kết quả
//        }
//
//        private String decodeQRCode ( byte[] imageData) throws IOException, NotFoundException {
//            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
//            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
//            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, getHints());
//            return qrCodeResult.getText();
//        }
//
//        private Map<DecodeHintType, Object> getHints () {
//            Map<DecodeHintType, Object> hints = new HashMap<>();
//            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
//            return hints;
//        }
//
//    }
//>>>>>>> a53247d9066017830196c52a31c0a7dcf90fda22
//
//
//
//
//
package com.example.demo.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.demo.Entitys.ChucVu;
import com.example.demo.Entitys.NhanVien;
import com.example.demo.Repository.ChucVuRepository;
import com.example.demo.Repository.DiaChiRepository;
import com.example.demo.Repository.NhanVienRepository;
import com.example.demo.Request.NhanVienRequest;
import com.example.demo.Utils.RepoUtuils;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import jakarta.servlet.http.HttpSession;
//import jakarta.validation.Valid;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
//import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;


@Controller
@RequestMapping({"nhan-vien"})
public class NhanVienController extends RepoUtuils {
    private final NhanVienRepository nvRepo;
    private final DiaChiRepository diaChiRepository;
    private final ChucVuRepository  chucVuRepository;

    public NhanVienController(NhanVienRepository nvRepo, DiaChiRepository diaChiRepository,ChucVuRepository chucVuRepository) {
        this.nvRepo = nvRepo;
        this.diaChiRepository = diaChiRepository;
        this.chucVuRepository = chucVuRepository;
    }

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    Cloudinary cloudinary;

    @Autowired
    ChucVuRepository chucVuRe;

    String success;
    String error;


    @GetMapping("/route")
    public String yourHandlerMethod() {
        return "redirect:/nhan-vien/hien-thi";
    }

    @GetMapping({"hien-thi"})
    public String hienThi(Model model, @RequestParam("page") Optional<Integer> pageParam, HttpSession session) {
        int page = (Integer) pageParam.orElse(0);
        Pageable p = PageRequest.of(page, 5, Sort.by(Direction.DESC, new String[]{"id"}));
        Page<NhanVien> listNV = this.nvRepo.findAll(p);
        model.addAttribute("listNV", listNV);
        model.addAttribute("success", this.success);
        model.addAttribute("error", this.error);
        model.addAttribute("message_title1", "Quản lý nhân viên");
        model.addAttribute("message_title2", "Danh sách nhân viên");
        session.removeAttribute("SSnhanVien");

        this.success = null;
        return "admin/nhan-vien/index";
    }


    @GetMapping({"view-add"})
    public String viewAdd(Model model, HttpSession session) {
        NhanVien nhanVien = (NhanVien) session.getAttribute("SSnhanVien");
        NhanVien nv = new NhanVien(); // Tạo một đối tượng mới
        if (nhanVien != null) {
            nv.setTenNhanVien(nhanVien.getTenNhanVien());
            nv.setGioiTinh(nhanVien.getGioiTinh());
            nv.setCCCD(nhanVien.getCCCD());
            nv.setNgaySinh(nhanVien.getNgaySinh());
        }
        nv.setIdChucVu(BigInteger.valueOf(2));
        model.addAttribute("lstChucVu",chucVuRepository.findAllByTrangThai(1));
        model.addAttribute("message_title1", "Quản lý tài khoản");
        model.addAttribute("message_title2", "Thêm nhân viên");
        model.addAttribute("nhanVien", nv);
        return "admin/nhan-vien/add";
    }


    public static String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute("nhanVien") NhanVienRequest nhanVienRequest,
                      BindingResult result, HttpSession session,
                      Model model, @RequestParam("avatar") MultipartFile file) throws IOException {
        String image = "/assets/img/avatars/logo.png";
        if (result.hasErrors()) {
            System.out.println("co loi");
            model.addAttribute("lstChucVu",chucVuRepository.findAllByTrangThai(1));
            model.addAttribute("message_title1", "Quản lý khách hàng");
            model.addAttribute("message_title2", "Cập nhật khách hàng");
            return "admin/nhan-vien/add";
        }
        if (!file.isEmpty()) {
            Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            image = (String) r.get("secure_url");
        }
        try {
            String chuoi = nhanVienRequest.getTenNhanVien();
            String[] cacTu = chuoi.split("\\s+");
            StringBuilder ketQua = new StringBuilder();
            String truong = removeAccents(cacTu[cacTu.length - 1]);
            ketQua.append(truong);
            for (int i = 0; i < cacTu.length - 1; i++) {
                if (cacTu[i].length() > 0) {
                    ketQua.append(cacTu[i].charAt(0));
                }
            }

            NhanVien nv = new NhanVien();
            System.out.println(ketQua.toString());
            Optional<NhanVien> maxIdNhanVien = nvRepo.findMaId();
            int maxId = maxIdNhanVien.isPresent() ? Integer.parseInt(String.valueOf(maxIdNhanVien.get().getId())) + 1 : 0;
            System.out.println(maxId);
            nv.setMaNhanVien(ketQua.toString().toLowerCase(Locale.ROOT) + "NV" + maxId);
            nv.setTenNhanVien(nhanVienRequest.getTenNhanVien());
            nv.setSdt(nhanVienRequest.getSdt());
            nv.setEmail(nhanVienRequest.getEmail());
            nv.setCCCD(nhanVienRequest.getCCCD());
            nv.setNgaySinh(nhanVienRequest.getNgaySinh());
            nv.setDiaChiCuThe(nhanVienRequest.getDiaChiCuThe());
            nv.setTinh(nhanVienRequest.getTinh());
            nv.setQuan(nhanVienRequest.getQuan());
            nv.setPhuong(nhanVienRequest.getPhuong());
            nv.setGhiChu(nhanVienRequest.getGhiChu());
            nv.setGioiTinh(nhanVienRequest.getGioiTinh());
            nv.setTrangThai(1);
            nv.setIdChucVu(BigInteger.valueOf(1));
            nv.setNgayTao(new Date());
            nv.setAnhNhanVien(image);
            nv.setIdChucVu(nhanVienRequest.getIdChucVu());

            Random random = new Random();
            int randomNumber = random.nextInt(9000) + 1000;
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(nv.getEmail());
            msg.setSubject("CREATE ACCOUNT");
            msg.setText("Username: " + nv.getEmail() + "/ Password:" + randomNumber);
            javaMailSender.send(msg);
            nv.setMatKhau(String.valueOf(randomNumber));
            this.nvRepo.save(nv);
            this.success = "Thêm nhân viên thành công";
            session.removeAttribute("SSnhanVien");
        } catch (Exception e) {
            this.success = null;
            e.printStackTrace();
            return "redirect:/error";
        }
        return "redirect:/nhan-vien/hien-thi";
    }

    @GetMapping({"view-update/{id}"})
    public String viewUpdate(Model model, @PathVariable("id") NhanVien nv) {
        model.addAttribute("nhanVien", nv);
        model.addAttribute("maNV", "MÃ NHÂN VIÊN: " + nv.getMaNhanVien());
        model.addAttribute("lstChucVu",chucVuRepository.findAllByTrangThai(1));
        model.addAttribute("message_title1", "Quản lý tài khoản");
        model.addAttribute("message_title2", "Sửa thông tin nhân viên");
        return "admin/nhan-vien/update";
    }

    @PostMapping("/update/{id}")
    public String update(@Valid @ModelAttribute("nhanVien") NhanVienRequest nhanVienRequest,
                         BindingResult result, Model model,
                         @PathVariable("id") BigInteger idNV,
                         @RequestParam("avatar") MultipartFile file) throws IOException {
        if (result.hasErrors()) {
            System.out.println("co loi");
            model.addAttribute("lstChucVu",chucVuRepository.findAllByTrangThai(1));
            model.addAttribute("message_title1", "Quản lý khách hàng");
            model.addAttribute("message_title2", "Cập nhật khách hàng");
            return "admin/nhan-vien/update";
        }

        Optional<NhanVien> optionalNhanVien = nvRepo.findById(idNV);
        if (!optionalNhanVien.isPresent()) {
            return "redirect:/error";
        }
        String image = "/assets/img/avatars/logo.png";
        NhanVien nhanVien = optionalNhanVien.get();
        nhanVien.setTenNhanVien(nhanVienRequest.getTenNhanVien());
        nhanVien.setSdt(nhanVienRequest.getSdt());
        nhanVien.setCCCD(nhanVienRequest.getCCCD());
        nhanVien.setEmail(nhanVienRequest.getEmail());
        nhanVien.setNgaySinh(nhanVienRequest.getNgaySinh());
        nhanVien.setTinh(nhanVienRequest.getTinh());
        nhanVien.setQuan(nhanVienRequest.getQuan());
        nhanVien.setPhuong(nhanVienRequest.getPhuong());
        nhanVien.setDiaChiCuThe(nhanVienRequest.getDiaChiCuThe());
        nhanVien.setGhiChu(nhanVienRequest.getGhiChu());
        nhanVien.setGioiTinh(nhanVienRequest.getGioiTinh());
        nhanVien.setIdChucVu(nhanVienRequest.getIdChucVu());
        nhanVien.setNgaySua(new Date());
        if (!file.isEmpty()) {
            Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            image = (String) r.get("secure_url");
        }
        nhanVien.setAnhNhanVien(image);
        try {
            this.nvRepo.save(nhanVien);
            this.success = "Cập nhật nhân viên thành công";
        } catch (Exception e) {
            this.success = null;
            e.printStackTrace();
            return "redirect:/error";
        }
        return "redirect:/nhan-vien/hien-thi";
    }

    @GetMapping({"delete/{id}"})
    public String delete(@PathVariable("id") NhanVien nv) {
        System.out.println(nv.getTenNhanVien());
        nv.setTrangThai(0);
        this.nvRepo.save(nv);
        this.success = "Xoá nhân viên thành công";
        return "redirect:/nhan-vien/hien-thi";
    }

    @RequestMapping("/search")
    public String search(@RequestParam("page") Optional<Integer> pageParam,
                         @RequestParam(defaultValue = "", name = "search") String search,
                         @RequestParam(defaultValue = "", name = "status") Integer status,
                         Model model) {
        int page = pageParam.orElse(0);
        Pageable p = PageRequest.of(page, 5);
        Page<NhanVien> nhanVienPage = nvRepo.findAllBySearch(search, status, p);
        NhanVien nv = new NhanVien();
        model.addAttribute("checkSearch", true);
        model.addAttribute("listNV", nhanVienPage);
        model.addAttribute("nhanVien", nv);
        model.addAttribute("message_title1", "Quản lý nhân viên");
        model.addAttribute("message_title2", "Danh sách nhân viên");
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "admin/nhan-vien/index";
    }

    @PostMapping("/import")
    public String importExcel(@RequestPart("file") MultipartFile multipartFile, HttpSession session) {
        if (multipartFile.isEmpty()) {
            return "redirect:/nhan-vien/hien-thi";
        }
        try (InputStream excelFile = multipartFile.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();
            Iterator<Row> iterator = datatypeSheet.iterator();
            Row firstRow = iterator.next();
            Cell firstCell = firstRow.getCell(0);
            System.out.println(firstCell.getStringCellValue());
            List<NhanVien> listOfNhanVien = new ArrayList<NhanVien>();
            int demMa = 0;
            int maId = 1; // Tạo biến để lưu trữ mã mới cho mỗi nhân viên
            Optional<NhanVien> maxIdNhanVien = nvRepo.findMaId();
            if (maxIdNhanVien.isPresent()) {
                maId = Integer.parseInt(String.valueOf(maxIdNhanVien.get().getId())) + 1;
            }

            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                NhanVien nv = new NhanVien();
                String chuoi = currentRow.getCell(0).getStringCellValue();
                String[] cacTu = chuoi.split("\\s+");
                StringBuilder ketQua = new StringBuilder();
                String truong = removeAccents(cacTu[cacTu.length - 1]);
                ketQua.append(truong);
                for (int i = 0; i < cacTu.length - 1; i++) {
                    if (cacTu[i].length() > 0) {
                        ketQua.append(cacTu[i].charAt(0));
                    }
                }
                nv.setMaNhanVien(ketQua.toString().toLowerCase(Locale.ROOT) + "NV" + (maId + demMa));
                nv.setTenNhanVien(currentRow.getCell(0).getStringCellValue());
                nv.setSdt(currentRow.getCell(1).toString());
                nv.setEmail(currentRow.getCell(2).getStringCellValue());
                nv.setCCCD(currentRow.getCell(3).getStringCellValue());
                nv.setDiaChiCuThe(currentRow.getCell(4).getStringCellValue());
                nv.setPhuong(currentRow.getCell(5).getStringCellValue());
                nv.setQuan(currentRow.getCell(6).getStringCellValue());
                nv.setTinh(currentRow.getCell(7).getStringCellValue());
//                nv.setIdChucVu(BigInteger.valueOf(Long.parseLong(currentRow.getCell(8).getStringCellValue())));

                Random random = new Random();
                int randomNumber = random.nextInt(9000) + 1000;
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(nv.getEmail());
                msg.setSubject("CREATE ACCOUNT");
                msg.setText("Username: " + nv.getEmail() + "/ Password:" + randomNumber);
                javaMailSender.send(msg);
                nv.setMatKhau(String.valueOf(randomNumber));
                nv.setTrangThai(1);
                XSSFCell cell = (XSSFCell) currentRow.getCell(8, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                Date ngaySinh = null;
                if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                    ngaySinh = cell.getDateCellValue();
                } else if (cell.getCellTypeEnum() == CellType.STRING) {
                    try {
                        LocalDate localDate = LocalDate.parse(cell.getStringCellValue());
                        ngaySinh = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                nv.setNgaySinh(ngaySinh);
                nv.setGioiTinh(currentRow.getCell(9).getStringCellValue().equals("Nam") ? true : false);
                nv.setNgayTao(new Date());
                Optional<ChucVu> chucVu = chucVuRepository.findFirstByTenChucVuIsLike(currentRow.getCell(10).getStringCellValue());
                if (chucVu.isPresent()) {
                    nv.setIdChucVu(chucVu.get().getId());
                } else {
                    nv.setIdChucVu(BigInteger.ONE);
                }
                nv.setTrangThai(ACTIVE);
                demMa++;
                listOfNhanVien.add(nv);
                System.out.println(nv.getTenNhanVien()+nv.getSdt());
            }
            workbook.close();

            List<NhanVien> listkhR = nvRepo.findAll();
            List<NhanVien> staffToRemove = new ArrayList<>();
            for (int i = 0; i < listkhR.size(); i++) {
                for (int j = 0; j < listOfNhanVien.size(); j++) {
                    if (listkhR.get(i).getSdt().equals(listOfNhanVien.get(j).getSdt())
                            || listkhR.get(i).getCCCD().equals(listOfNhanVien.get(j).getCCCD())
                            || listkhR.get(i).getEmail().equals(listOfNhanVien.get(j).getEmail())
                    ) {
                        staffToRemove.add(listOfNhanVien.get(j));
                    }
                }
            }
            listOfNhanVien.removeAll(staffToRemove);
            nvRepo.saveAll(listOfNhanVien);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            System.out.println("Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/nhan-vien/hien-thi";
        }
        return "redirect:/nhan-vien/hien-thi";

    }

    @PostMapping("/scanQRCode")
    public String scanQRCode (@RequestParam(value = "file", required = false) MultipartFile file, Model
            model, HttpSession session){
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
            NhanVien nhanVien = new NhanVien();
            nhanVien.setTenNhanVien(name);
            nhanVien.setCCCD(code);
            if (gender.equals("Nam")) {
                nhanVien.setGioiTinh(true);
            }
            if (gender.equals("Nữ")) {
                nhanVien.setGioiTinh(false);
            }
            SimpleDateFormat dateFormatInput = new SimpleDateFormat("ddMMyyyy");
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date ngaySinh = dateFormatInput.parse(birthdate); // Chuyển đổi từ ddMMyyyy sang Date
                String ngaySinhFormatted = dateFormatOutput.format(ngaySinh); // Chuyển đổi lại sang định dạng dd/MM/yyyy
                System.out.println("Ngày sinh: " + ngaySinhFormatted);
                nhanVien.setNgaySinh(ngaySinh);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            session.setAttribute("SSnhanVien", nhanVien);
            return "redirect:/nhan-vien/view-add";
        } catch (IOException | NotFoundException e) {
            model.addAttribute("error", "QR code not found or couldn't be decoded.");
        }
        return "admin/scan/qr_result"; // Trả về trang HTML để hiển thị kết quả
    }

    private String decodeQRCode ( byte[] imageData) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, getHints());
        return qrCodeResult.getText();
    }

    private Map<DecodeHintType, Object> getHints () {
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        return hints;
    }

}






