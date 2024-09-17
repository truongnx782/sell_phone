package com.example.demo.Controller;

import com.example.demo.Entitys.ManHinh;
import com.example.demo.Repository.ManHinhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigInteger;
import java.util.Optional;

@Controller

@RequestMapping("man-hinh")
public class ManHinhController {

    @Autowired
    private ManHinhRepository manHinhRepository;

    @GetMapping("/index")
    public String index(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<ManHinh> data = manHinhRepository.findAll(pageable);
        model.addAttribute("data", data);
        // Kiểm tra xem có thông báo "success" trong redirectAttributes hay không
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách màn hình");
        return "admin/ql_thuoc_tinh/man_hinh/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách màn hình");
        model.addAttribute("message_title2", "Thêm mới màn hình");
        return "admin/ql_thuoc_tinh/man_hinh/add";
    }

    @PostMapping("/store")
    public String store(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (manHinhRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(manHinhRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            model.addAttribute("ten", ten);
            model.addAttribute("message_title1", "Danh sách màn hình");
            model.addAttribute("message_title2", "Thêm mới màn hình");
            return "admin/ql_thuoc_tinh/man_hinh/add";
        }else {
            ManHinh manHinh = new ManHinh();
            manHinh.setTen(ten);
            manHinh.setTrangThai(1);
            manHinhRepository.save(manHinh);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/man-hinh/index";

        }
    }


    @GetMapping("search")
    public String search(@RequestParam("search") String ten, Model model, @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<ManHinh> findSearch = manHinhRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách màn hình");
        return "admin/ql_thuoc_tinh/man_hinh/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        ManHinh manHinh = manHinhRepository.getReferenceById(id);
        model.addAttribute("manHinh", manHinh);
        model.addAttribute("message_title1", "Danh sách màn hình");
        model.addAttribute("message_title2", "Cập nhật màn hình");
        return "admin/ql_thuoc_tinh/man_hinh/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            RedirectAttributes redirectAttributes,
            Model model)
    {

        ManHinh manHinhOld = manHinhRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (manHinhRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(manHinhOld.getTen()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(manHinhRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(manHinhOld.getTen())){
                model.addAttribute("loi",  "Loi_Trung!");

            }
            manHinhOld.setTen(ten);
            model.addAttribute("manHinh", manHinhOld);
            model.addAttribute("message_title1", "Danh sách màn hình");
            model.addAttribute("message_title2", "Cập nhật màn hình");
            return "admin/ql_thuoc_tinh/man_hinh/update";
        }else {
            manHinhOld.setTen(ten);
            manHinhRepository.save(manHinhOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/man-hinh/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (manHinhRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
                return ResponseEntity.ok(1);
            }
            else {
                model.addAttribute("loi",  "Loi_Trung!");
                return ResponseEntity.ok(2);
            }
        }else {
            ManHinh manHinh = new ManHinh();
            manHinh.setTen(ten);
            manHinh.setTrangThai(1);
            manHinhRepository.save(manHinh);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return  ResponseEntity.ok(3);
        }
    }
}
