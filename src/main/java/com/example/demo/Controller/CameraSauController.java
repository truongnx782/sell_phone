package com.example.demo.Controller;

import com.example.demo.Entitys.CameraSau;
import com.example.demo.Entitys.Chip;
import com.example.demo.Repository.CameraSauRepository;
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
@RequestMapping("cameraSau")
public class CameraSauController {

    @Autowired
    private CameraSauRepository cameraSauRepository;

    @GetMapping("/index")
    public String index(
            Model model,
            @RequestParam("page") Optional<Integer> pageParam,
            RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<CameraSau> data = cameraSauRepository.findAll(pageable);
        model.addAttribute("data", data);
        // Kiểm tra xem có thông báo "success" trong redirectAttributes hay không
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách camera sau");
        return "admin/ql_thuoc_tinh/camera_sau/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách camera sau");
        model.addAttribute("message_title2", "Thêm mới camera sau");
        return "admin/ql_thuoc_tinh/camera_sau/add";
    }

    @PostMapping("/store")
    public String store(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {
        if (ten.trim().length() == 0 || (cameraSauRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(cameraSauRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            model.addAttribute("ten", ten);
            model.addAttribute("message_title1", "Danh sách camera sau");
            model.addAttribute("message_title2", "Thêm mới camera sau");
            return "admin/ql_thuoc_tinh/camera_sau/add";
        }else {
            System.out.println("Tên cam "+ten);
            CameraSau cameraSau = new CameraSau();
            cameraSau.setTen(ten);
            cameraSau.setTrangThai(1);
            cameraSauRepository.save(cameraSau);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/cameraSau/index";
        }

    }


    @GetMapping("/search")
    public String search(
            @RequestParam("search") String ten,
            Model model,
            @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<CameraSau> findSearch = cameraSauRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        return "admin/ql_thuoc_tinh/camera_sau/index";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        Optional<CameraSau> cameraSau = cameraSauRepository.findById(id);
        CameraSau c = cameraSauRepository.getReferenceById(id);
        model.addAttribute("cameraSau", c);
        model.addAttribute("message_title1", "Danh sách camera sau");
        model.addAttribute("message_title2", "Cập nhật camera sau");
        return "admin/ql_thuoc_tinh/camera_sau/update1";
    }

    @PostMapping("/update/{id}")
    public String update(
            Model model,
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            RedirectAttributes redirectAttributes)
    {

        CameraSau cameraSauOld = cameraSauRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (cameraSauRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(cameraSauOld.getTen()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(cameraSauRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(cameraSauOld.getTen())){
                model.addAttribute("loi",  "Loi_Trung!");

            }
            cameraSauOld.setTen(ten);
            model.addAttribute("cameraSau", cameraSauOld);
            model.addAttribute("ten", ten);
            model.addAttribute("message_title1", "Danh sách camera sau");
            model.addAttribute("message_title2", "Cập nhật camera sau");
            return "admin/ql_thuoc_tinh/camera_sau/update1";
        }else {
            cameraSauOld.setTen(ten);
            cameraSauRepository.save(cameraSauOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/cameraSau/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(@RequestParam("ten") String ten,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (ten.trim().length() == 0 || (cameraSauRepository.findByTenIsLike(ten.trim()) != null)) {
            if (ten.trim().length() == 0) {
                model.addAttribute("loi", "Loi_Trong!");
                return ResponseEntity.ok(1);
            } else{
                model.addAttribute("loi", "Loi_Trung!");
                model.addAttribute("ten", ten);
                return ResponseEntity.ok(2);
            }
        } else {
            System.out.println("Tên cam " + ten);
            CameraSau cameraSau = new CameraSau();
            cameraSau.setTen(ten);
            cameraSau.setTrangThai(1);
            cameraSauRepository.save(cameraSau);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return ResponseEntity.ok(3);
        }
    }
}
