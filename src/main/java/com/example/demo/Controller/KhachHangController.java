package com.example.demo.Controller;

import com.example.demo.Entitys.DiaChi;
import com.example.demo.Entitys.HoaDon;
import com.example.demo.Entitys.KhachHang;
import com.example.demo.Repository.DiaChiRepository;
import com.example.demo.Repository.HoaDonRepository;
import com.example.demo.Repository.KhachHangRepository;
import com.example.demo.Request.KhachHangRequest;
import com.example.demo.Utils.RepoUtuils;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.Normalizer;
import java.util.*;

@Controller
@RequestMapping("/khach-hang")
public class KhachHangController extends RepoUtuils {
    @Autowired
    JavaMailSender javaMailSender;
    private final KhachHangRepository khachHangRepository;
    private final DiaChiRepository diaChiRepository;
    private final HoaDonRepository hoaDonRepository;
    public KhachHangController(KhachHangRepository khachHangRepository, DiaChiRepository diaChiRepository, HoaDonRepository hoaDonRepository) {
        this.khachHangRepository = khachHangRepository;
        this.diaChiRepository = diaChiRepository;
        this.hoaDonRepository = hoaDonRepository;
    }

    String success;
    String error;

    @GetMapping("/route")
    public String yourHandlerMethod() {
        return "redirect:/khach-hang/hien-thi";
    }

    @GetMapping("/hien-thi")
    public String hienThi(@RequestParam("page") Optional<Integer> pageParam, Model model) {
        int page = pageParam.orElse(0);
        Pageable p = PageRequest.of(page, 5);
        Page<KhachHang> khachHangPage = khachHangRepository.findAllBy(p);
        List<DiaChi> listDC = diaChiRepository.findAll();
        KhachHang kh = new KhachHang();
        model.addAttribute("KhachHang", kh);
        model.addAttribute("items", khachHangPage);
        model.addAttribute("listDC", listDC);
        model.addAttribute("message_title1", "Quản lý khách hàng");
        model.addAttribute("message_title2", "Danh sách khách hàng");
        model.addAttribute("success", this.success);
        model.addAttribute("error", this.error);
        this.success = null;
        return "admin/KhachHang/index";
    }

    @GetMapping("/view-add")
    public String add(Model model, HttpSession session) {
        KhachHang kh = new KhachHang();
        DiaChi dc = new DiaChi();
        model.addAttribute("KhachHang", kh);
        model.addAttribute("DiaChi", dc);
        model.addAttribute("listDC", diaChiRepository.findAllBy(WAIT));
        model.addAttribute("message_title1", "Quản lý khách hàng");
        model.addAttribute("message_title2", "Thêm khách hàng");
        return "admin/KhachHang/add";
    }


    public static String removeAccents(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    @PostMapping("/store")
    public String create(@Valid @ModelAttribute("KhachHang") KhachHangRequest khachHangRequest,
                         BindingResult result, Model model) throws Exception {
        try {
            if (result.hasErrors()) {
                System.out.println("co loi");
                DiaChi dc = new DiaChi();
                model.addAttribute("DiaChi", dc);
                List<DiaChi> list = diaChiRepository.findAllBy(WAIT);
                model.addAttribute("listDC", list);
                System.out.println(khachHangRequest.getAnhKhachHang());
                model.addAttribute("message_title1", "Quản lý khách hàng");
                model.addAttribute("message_title2", "Thêm khách hàng");
                model.addAttribute("messageDiaChi", "Địa chỉ không được để trống");
                return "admin/KhachHang/add";
            }

            String chuoi = khachHangRequest.getTenKhachHang();
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
            System.out.println(maxId);
            KhachHang khachHang = new KhachHang();
            khachHang.setMaKhachHang(ketQua.toString().toLowerCase(Locale.ROOT) + "KH" + maxId);
            khachHang.setTenKhachHang(khachHangRequest.getTenKhachHang());
            khachHang.setSdt(khachHangRequest.getSdt());
            khachHang.setEmail(khachHangRequest.getEmail());
            khachHang.setMatKhau(khachHangRequest.getMatKhau());
            khachHang.setTrangThai(ACTIVE);
            String image = "logo.png";
            khachHang.setAnhKhachHang("/assets/img/avatars/" + image);

            Random random = new Random();
            int randomNumber = random.nextInt(9000) + 1000;
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(khachHang.getEmail());
            msg.setSubject("CREATE ACCOUNT");
            msg.setText("Username: " + khachHang.getEmail() + "/ Password:" + randomNumber);
            javaMailSender.send(msg);

            khachHang.setMatKhau(String.valueOf(randomNumber));
            KhachHang khachHangNew = khachHangRepository.save(khachHang);
            List<DiaChi> list = diaChiRepository.findAllBy(WAIT);

            Iterator<DiaChi> iterator = list.iterator();
            System.out.println("Id địa chỉ: "+ khachHangRequest.getIdDiaChi());
            while (iterator.hasNext()) {
                DiaChi diaChi = iterator.next();
                if (diaChi.getId().equals(khachHangRequest.getIdDiaChi())) {
                    diaChi.setIdKhachHang(khachHangNew.getId());
                    diaChi.setTrangThai(DEFAULT);
                    diaChiRepository.save(diaChi);
                    iterator.remove();
                } else {
                    diaChi.setIdKhachHang(khachHangNew.getId());
                    diaChi.setTrangThai(1);
                    diaChiRepository.save(diaChi);
                }
            }
            diaChiRepository.saveAll(list);
            this.success = "Thêm khách hàng thành công";
            return "redirect:/khach-hang/hien-thi";
        } catch (Exception ex) {
            this.success = null;
            ex.printStackTrace();
            return null;
        }

    }

    @GetMapping("delete/{id}")
    public String delete(@PathVariable("id")  BigInteger id) {
        try {
            System.out.println(id);
            Optional<KhachHang> khachHangOptional = khachHangRepository.findById(id);
            KhachHang khachHang = khachHangOptional.get();
            khachHang.setTrangThai(IN_ACTIVE);
            System.out.println(khachHang.getId());
            khachHangRepository.save(khachHang);
            this.success = "Xoá khách hàng thành công";
            return "redirect:/khach-hang/hien-thi";
        } catch (Exception ex) {
            this.success = null;
            ex.printStackTrace();
            return null;
        }
    }

    @GetMapping("edit/{id}")
    public String edit(@PathVariable("id") BigInteger id, Model model) {
        try {
            DiaChi dc = new DiaChi();
            List<DiaChi> ldc = diaChiRepository.findAllBy(id, ACTIVE, DEFAULT);
            Optional<KhachHang> khachHangOptional = khachHangRepository.findById(id);
            model.addAttribute("listDC", ldc);
            model.addAttribute("DiaChi", dc);
            model.addAttribute("KhachHang", khachHangOptional.get());
            model.addAttribute("message_title1", "Quản lý khách hàng");
            model.addAttribute("message_title2", "Cập nhật khách hàng");
            return "admin/KhachHang/form";
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @PostMapping("/update/{id}")
    public String update(@Valid @ModelAttribute("KhachHang") KhachHangRequest khachHangRequest,
                         BindingResult result, @PathVariable("id") KhachHang khachHang,
                         @RequestParam(name = "idDiaChi", required = false) BigInteger idDiaChi,
                         Model model) {
        try {
            if (idDiaChi == null) {
                DiaChi dc = new DiaChi();
                List<DiaChi> ldc = diaChiRepository.findAllBy(khachHang.getId(), ACTIVE, DEFAULT);
                model.addAttribute("listDC", ldc);
                model.addAttribute("DiaChi", dc);
                model.addAttribute("message_title1", "Quản lý khách hàng");
                model.addAttribute("message_title2", "Cập nhật khách hàng");
                model.addAttribute("messageDiaChi", "Địa chỉ không được để trống");
                return "admin/KhachHang/form";
            }

            if (result.hasErrors()) {
                System.out.println("co loi");
                System.out.println(khachHang.getId());
                List<DiaChi> ldc = diaChiRepository.findAllBy(khachHang.getId(), ACTIVE, DEFAULT);
                model.addAttribute("listDC", ldc);
                DiaChi dc = new DiaChi();
                model.addAttribute("DiaChi", dc);
                model.addAttribute("message_title1", "Quản lý khách hàng");
                model.addAttribute("message_title2", "Cập nhật khách hàng");
                model.addAttribute("anhKhachHang2", khachHang.getAnhKhachHang());
                this.success = "Cập nhật khách hàng thành công";
                return "admin/KhachHang/form";
            }
            List<DiaChi> ldc = diaChiRepository.findAllBy(khachHang.getId(), DEFAULT);
            ldc.forEach(diaChi -> {
                diaChi.setTrangThai(1);
            });
            diaChiRepository.saveAll(ldc);
            Optional<DiaChi> optionalDiaChi = diaChiRepository.findById(idDiaChi);
            DiaChi diaChi = optionalDiaChi.get();
            diaChi.setTrangThai(DEFAULT);
            diaChiRepository.save(diaChi);
            khachHang.setTenKhachHang(khachHangRequest.getTenKhachHang());
            khachHang.setEmail(khachHangRequest.getEmail());
            khachHang.setMatKhau(khachHangRequest.getMatKhau());
            khachHang.setSdt(khachHangRequest.getSdt());
            khachHang.setTrangThai(ACTIVE);
            khachHangRepository.save(khachHang);
            this.success = "Cập nhật khách hàng thành công";
            return "redirect:/khach-hang/hien-thi";
        } catch (Exception ex) {
            this.success = null;
            ex.printStackTrace();
            return null;
        }
    }

    @RequestMapping("/search")
    public String search(@RequestParam("page") Optional<Integer> pageParam,
                         @RequestParam(defaultValue = "", name = "search") String search,
                         @RequestParam(defaultValue = "", name = "status") Integer status, Model model) {
        int page = pageParam.orElse(0);
        Pageable p = PageRequest.of(page, 5);
        Page<KhachHang> khachHangPage = khachHangRepository.findAllBySearch(search, status, p);
        KhachHang kh = new KhachHang();
        List<DiaChi> listDC = diaChiRepository.findAll();
        model.addAttribute("listDC", listDC);
        model.addAttribute("checkSearch", true);
        model.addAttribute("items", khachHangPage);
        model.addAttribute("KhachHang", kh);
        model.addAttribute("message_title1", "Quản lý khách hàng");
        model.addAttribute("message_title2", "Danh sách khách hàng");
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "admin/KhachHang/index";
    }

    @PostMapping("/import")
    public String importExcel(@RequestPart("file") MultipartFile multipartFile, HttpSession session) {
        if (multipartFile.isEmpty()) {
            return "redirect:/khach-hang/hien-thi";
        }
        try (InputStream excelFile = multipartFile.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();
            Iterator<Row> iterator = datatypeSheet.iterator();
            Row firstRow = iterator.next();
            Cell firstCell = firstRow.getCell(0);
            System.out.println(firstCell.getStringCellValue());
            List<KhachHang> listOfCustomer = new ArrayList<KhachHang>();

            int demMa = 0;
            int maId = 1; // Tạo biến để lưu trữ mã mới cho mỗi nhân viên
            Optional<KhachHang> maxIdNhanVien = khachHangRepository.findMaId();
            if (maxIdNhanVien.isPresent()) {
                maId = Integer.parseInt(String.valueOf(maxIdNhanVien.get().getId())) + 1;
            }
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                KhachHang customer = new KhachHang();
                customer.setTenKhachHang(currentRow.getCell(0).getStringCellValue());
                customer.setSdt(currentRow.getCell(1).toString());
                System.out.println("email: "+currentRow.getCell(2).getStringCellValue());
                customer.setEmail(currentRow.getCell(2).getStringCellValue());
                customer.setTrangThai(ACTIVE);
                String chuoi = currentRow.getCell(0).getStringCellValue();
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
                customer.setMaKhachHang(ketQua.toString().toLowerCase(Locale.ROOT) + "KH" + (maId + demMa));

                Random random = new Random();
                int randomNumber = random.nextInt(9000) + 1000;
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(customer.getEmail());
                msg.setSubject("CREATE ACCOUNT");
                msg.setText("Username: " + customer.getEmail() + "/ Password:" + randomNumber);
                javaMailSender.send(msg);
                customer.setMatKhau(String.valueOf(randomNumber));

                customer.setTrangThai(1);
                demMa++;
                listOfCustomer.add(customer);

            }
            System.out.println("List khách hàng: "+ listOfCustomer.size());
            workbook.close();

            List<KhachHang> listkhR = khachHangRepository.findAll();
            List<KhachHang> customersToRemove = new ArrayList<>();
            for (int i = 0; i < listkhR.size(); i++) {
                for (int j = 0; j < listOfCustomer.size(); j++) {
                    if (listkhR.get(i).getSdt().equals(listOfCustomer.get(j).getSdt())
                            || listkhR.get(i).getEmail().equals(listOfCustomer.get(j).getEmail())
                    ) {
                        customersToRemove.add(listOfCustomer.get(j));

                    }
                }
            }
            listOfCustomer.removeAll(customersToRemove);
            khachHangRepository.saveAll(listOfCustomer);
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            System.out.println("Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/khach-hang/hien-thi";
        }
        return "redirect:/khach-hang/hien-thi";
    }

    @GetMapping("detail/{id}")
    public String detail(@PathVariable("id") BigInteger id, Model model) {
        try {
            KhachHang kh = khachHangRepository.getReferenceById(id);
            List<HoaDon> ldc = hoaDonRepository.findAllByIdKhachHang(kh);
            model.addAttribute("items", ldc);
            model.addAttribute("message_title1", "Quản lý khách hàng");
            model.addAttribute("message_title2", "Chi tiết");
            return "admin/KhachHang/donHang";
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}



