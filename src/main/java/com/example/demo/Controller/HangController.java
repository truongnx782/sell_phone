package com.example.demo.Controller;


import com.example.demo.Entitys.Hang;
import com.example.demo.Repository.HangRepository;
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
@RequestMapping("hang")
public class HangController {

    @Autowired
    private HangRepository hangRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> pageParam,
                        RedirectAttributes redirectAttributes)
    {

        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Hang> data = hangRepository.findAll(pageable);
        model.addAttribute("data", data);
        // Kiểm tra xem có thông báo "success" trong redirectAttributes hay không
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách hãng");
        return "admin/ql_thuoc_tinh/hang/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách hãng");
        model.addAttribute("message_title2", "Thêm mới hãng");
        return "admin/ql_thuoc_tinh/hang/add";
    }

    @PostMapping("/store")
    public String store(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {
        if (ten.trim().length() == 0 || (hangRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(hangRepository.findByTenIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
                model.addAttribute("ten", ten);
            }
            model.addAttribute("message_title1", "Danh sách hãng");
            model.addAttribute("message_title2", "Thêm mới hãng");
            return "admin/ql_thuoc_tinh/hang/add";
        }else {
            Hang hang = new Hang();
            hang.setTen(ten);
            hang.setTrangThai(1);
            hangRepository.save(hang);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/hang/index";
        }
    }

    @GetMapping("search")
    public String search(
            @RequestParam("search") String ten,
            Model model,
            @RequestParam("page") Optional<Integer> pageParam)
    {

        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<Hang> findSearch = hangRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("search", ten);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách hãng");
        return "admin/ql_thuoc_tinh/hang/index";
    }

    @GetMapping("edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {

        Hang c = hangRepository.getReferenceById(id);
        model.addAttribute("hang", c);
        model.addAttribute("message_title1", "Danh sách hãng");
        model.addAttribute("message_title2", "Cập nhật mới hãng");
        return "admin/ql_thuoc_tinh/hang/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            RedirectAttributes redirectAttributes, Model model)
    {

        Hang hangOld = hangRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (hangRepository.findByTenIsLike(ten.trim()) != null && !ten.trim().equals(hangOld.getTen()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(hangRepository.findByTenIsLike(ten.trim()) != null  && !ten.trim().equals(hangOld.getTen())){
                model.addAttribute("loi",  "Loi_Trung!");

            }
            hangOld.setTen(ten);
            model.addAttribute("hang", hangOld);
            model.addAttribute("message_title1", "Danh sách hãng");
            model.addAttribute("message_title2", "Cập nhật hãng");
            return "admin/ql_thuoc_tinh/hang/update";
        }else {
            hangOld.setTen(ten);
            hangRepository.save(hangOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/hang/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {
        if (ten.trim().length() == 0 || (hangRepository.findByTenIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
                return ResponseEntity.ok(1);
            }
            else {
                model.addAttribute("loi",  "Loi_Trung!");
                return ResponseEntity.ok(2);
            }
        }else {
            Hang hang = new Hang();
            hang.setTen(ten);
            hang.setTrangThai(1);
            hangRepository.save(hang);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return ResponseEntity.ok(3);
        }
    }

}
