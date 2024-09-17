package com.example.demo.Controller;


import com.example.demo.Entitys.ChucVu;
import com.example.demo.Entitys.SanPham;
import com.example.demo.Repository.ChucVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigInteger;
import java.util.Optional;

@Controller
@RequestMapping("chuc-vu")
public class ChucVuController {

    @Autowired
    private ChucVuRepository chucVuRepository;

    @GetMapping("/index")
    public String index(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            RedirectAttributes redirectAttributes) {

        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<ChucVu> data = chucVuRepository.findAll(pageable);
        model.addAttribute("data", data);
        // Kiểm tra xem có thông báo "success" trong redirectAttributes hay không
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý nhân viên");
        model.addAttribute("message_title2", "Danh sách chức vụ");
        return "admin/chucVu/index";
    }

    @GetMapping("/add")
    public String add() {
        return "admin/ql_thuoc_tinh/chucVu/add";
    }

    @PostMapping("/store")
    public String store(
            @RequestParam("tenChucVu") String ten,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (ten.trim().length() == 0 || (chucVuRepository.findByTenChucVuIsLike(ten.trim()) != null)) {
            if (ten.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
            } else if (chucVuRepository.findByTenChucVuIsLike(ten.trim()) != null) {
                model.addAttribute("loi", "Loi_Trung!");
                model.addAttribute("tenChucVu", ten);
            }
            model.addAttribute("message_title1", "Quản lý nhân viên");
            model.addAttribute("message_title2", "Thêm mới chức vụ");
            return "admin/chucVu/add";
        } else {
            Optional<ChucVu> maxIdSP = chucVuRepository.findMaId();
            BigInteger maxId = maxIdSP.isPresent() ? maxIdSP.get().getId().add(BigInteger.ONE) : BigInteger.ONE;
            ChucVu chucVu = new ChucVu();
            chucVu.setMaChucVu("CV" + maxId);
            chucVu.setTenChucVu(ten);
            chucVu.setTrangThai(1);
            chucVuRepository.save(chucVu);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/chuc-vu/index";
        }
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") BigInteger id) {
        int currentStatus = chucVuRepository.findById(id).get().getTrangThai();
        if (currentStatus == 1) {
            chucVuRepository.changeStatus(0, id);
        } else {
            chucVuRepository.changeStatus(1, id);
        }
        return "redirect:/chuc-vu/index";
    }

    @GetMapping("search")
    public String search(
            @RequestParam("search") String ten,
            Model model,
            @RequestParam("page") Optional<Integer> pageParam) {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<ChucVu> findSearch = chucVuRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("main", "search");
        model.addAttribute("message_title1", "Quản lý nhân viên");
        model.addAttribute("message_title2", "Danh sách chức vụ");
        return "admin/ChucVu/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id) {
        Optional<ChucVu> cv = chucVuRepository.findById(id);
        ChucVu c = chucVuRepository.getReferenceById(id);
        model.addAttribute("chucVu", c);
        model.addAttribute("message_title1", "Quản lý nhân viên");
        model.addAttribute("message_title2", "Cập nhật chức vụ");
        return "admin/chucVu/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            Model model,
            @PathVariable("id") ChucVu chucVu,
            @RequestParam("tenChucVu") String ten,
            RedirectAttributes redirectAttributes) {
        ChucVu chipOld = chucVuRepository.getReferenceById(chucVu.getId());
        if (ten.trim().length() == 0 || (chucVuRepository.findByTenChucVuIsLike(ten.trim()) != null && !ten.trim().equals(chipOld.getTenChucVu()))) {
            if (ten.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
                model.addAttribute("chucVu",chucVu );
            } else if (chucVuRepository.findByTenChucVuIsLike(ten.trim()) != null) {
                model.addAttribute("loi", "Loi_Trung!");
                model.addAttribute("tenChucVu", ten);
                model.addAttribute("chucVu",chucVu );

            }
            model.addAttribute("message_title1", "Quản lý nhân viên");
            model.addAttribute("message_title2", "Cập nhật chức vụ");
            return "admin/ChucVu/update";
        } else {
            ChucVu cv = chucVuRepository.getReferenceById(chucVu.getId());
            cv.setTenChucVu(ten);
            chucVuRepository.save(cv);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/chuc-vu/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickSave(
            @RequestParam("tenChucVu") String ten,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (ten.trim().isEmpty() || chucVuRepository.findByTenChucVuIsLike(ten.trim()) != null) {
            if (ten.trim().isEmpty()) {
                return ResponseEntity.ok(1);
            } else {
                model.addAttribute("loi", "Loi_Trung!");
                model.addAttribute("tenChucVu", ten);
                return ResponseEntity.ok(2);
            }
        } else {
            ChucVu cv = new ChucVu();
            cv.setTenChucVu(ten);
            cv.setTrangThai(1);
            chucVuRepository.save(cv);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return ResponseEntity.ok(3);
        }
    }
}
