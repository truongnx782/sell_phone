package com.example.demo.Controller;


import com.example.demo.Entitys.HeDieuHanh;
import com.example.demo.Repository.HeDieuHanhRepository;
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
@RequestMapping("he-dieu-hanh")
public class HeDieuHanhController {

    @Autowired
    private HeDieuHanhRepository heDieuHanhRepository;

    @GetMapping("/index")
    public String index(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<HeDieuHanh> data = heDieuHanhRepository.findAll(pageable);
        model.addAttribute("data", data);
        // Kiểm tra xem có thông báo "success" trong redirectAttributes hay không
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách hệ điều hành");
        return "admin/ql_thuoc_tinh/he_dieu_hanh/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách hệ điều hành");
        model.addAttribute("message_title2", "Thêm mới hệ điều hành");
        return "admin/ql_thuoc_tinh/he_dieu_hanh/add";
    }

    @PostMapping("/store")
    public String store(
            @RequestParam("ten") String ten,
            Model model,
            RedirectAttributes redirectAttributes
    )
    {
        if (ten.trim().length() == 0 || (heDieuHanhRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(heDieuHanhRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            model.addAttribute("ten", ten);
            model.addAttribute("message_title1", "Danh sách hệ điều hành");
            model.addAttribute("message_title2", "Thêm mới hệ điều hành");
            return "admin/ql_thuoc_tinh/he_dieu_hanh/add";
        }else {
            HeDieuHanh heDieuHanh = new HeDieuHanh();
            heDieuHanh.setTen(ten);
            heDieuHanh.setTrangThai(1);
            heDieuHanhRepository.save(heDieuHanh);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/he-dieu-hanh/index";
        }
    }


    @GetMapping("search")
    public String search(@RequestParam("search") String ten, Model model, @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<HeDieuHanh> findSearch = heDieuHanhRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách hệ điều hành");
        return "admin/ql_thuoc_tinh/he_dieu_hanh/index";
    }


    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        HeDieuHanh c = heDieuHanhRepository.getReferenceById(id);
        model.addAttribute("heDieuHanh", c);
        model.addAttribute("message_title1", "Danh sách hệ điều hành");
        model.addAttribute("message_title2", "Cập nhật hệ điều hành");
        return "admin/ql_thuoc_tinh/he_dieu_hanh/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            Model model,
            RedirectAttributes redirectAttributes)
    {
        HeDieuHanh heDieuHanhOld = heDieuHanhRepository.getReferenceById(id);

        if (ten.trim().length() == 0 || (heDieuHanhRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(heDieuHanhOld.getTen()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(heDieuHanhRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(heDieuHanhOld.getTen())){
                model.addAttribute("loi",  "Loi_Trung!");

            }
            heDieuHanhOld.setTen(ten);
            model.addAttribute("heDieuHanh", heDieuHanhOld);
            model.addAttribute("message_title1", "Danh sách hệ điều hành");
            model.addAttribute("message_title2", "Cập nhật hệ điều hành");
            return "admin/ql_thuoc_tinh/he_dieu_hanh/update";
        }else {
            heDieuHanhOld.setTen(ten);
            heDieuHanhRepository.save(heDieuHanhOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/he-dieu-hanh/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(
            @RequestParam("ten") String ten,
            Model model,
            RedirectAttributes redirectAttributes
    )
    {
        if (ten.trim().length() == 0 || (heDieuHanhRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
                return ResponseEntity.ok(1);
            }
            else {
                model.addAttribute("loi",  "Loi_Trung!");
                return  ResponseEntity.ok(2);
            }
        }else {
            HeDieuHanh heDieuHanh = new HeDieuHanh();
            heDieuHanh.setTen(ten);
            heDieuHanh.setTrangThai(1);
            heDieuHanhRepository.save(heDieuHanh);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return  ResponseEntity.ok(3);
        }
    }


}
