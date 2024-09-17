package com.example.demo.Controller;


import com.example.demo.Entitys.RAM;
import com.example.demo.Repository.RAMRepository;
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
@RequestMapping("ram")
public class RamController {

    @Autowired
    private RAMRepository ramRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> pageParam,
                        RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<RAM> data = ramRepository.findAll(pageable);
        model.addAttribute("data", data);
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách ram");
        return "admin/ql_thuoc_tinh/ram/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách ram");
        model.addAttribute("message_title2", "Thêm mới ram");
        return "admin/ql_thuoc_tinh/ram/add";
    }

    @PostMapping("/store")
    public String store(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (ramRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(ramRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            model.addAttribute("ten", ten);
            model.addAttribute("message_title1", "Danh sách ram");
            model.addAttribute("message_title2", "Thêm mới ram");
            return "admin/ql_thuoc_tinh/ram/add";
        }else {
            RAM ram = new RAM();
            ram.setTen(ten);
            ram.setTrangThai(1);
            ramRepository.save(ram);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/ram/index";
        }
    }


    @GetMapping("search")
    public String search(@RequestParam("search") String ten, Model model, @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<RAM> findSearch = ramRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách ram");
        return "admin/ql_thuoc_tinh/ram/index";
    }


    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        RAM ram = ramRepository.getReferenceById(id);
        model.addAttribute("ram", ram);
        model.addAttribute("message_title1", "Danh sách ram");
        model.addAttribute("message_title2", "Cập nhật ram");
        return "admin/ql_thuoc_tinh/ram/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            Model model,
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            RedirectAttributes redirectAttributes)
    {

        RAM ramOld = ramRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (ramRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(ramOld.getTen()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(ramRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(ramOld.getTen())){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            ramOld.setTen(ten);
            model.addAttribute("ram", ramOld);
            model.addAttribute("message_title1", "Danh sách ram");
            model.addAttribute("message_title2", "Cập nhật ram");
            return "admin/ql_thuoc_tinh/ram/update";
        }else {
            ramOld.setTen(ten);
            ramRepository.save(ramOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/ram/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (ramRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
                return ResponseEntity.ok(1);
            }
            else {
                model.addAttribute("loi",  "Loi_Trung!");
                model.addAttribute("ten", ten);
                return ResponseEntity.ok(2);
            }
        }else {
            RAM ram = new RAM();
            ram.setTen(ten);
            ram.setTrangThai(1);
            ramRepository.save(ram);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return  ResponseEntity.ok(3);
        }
    }
}
