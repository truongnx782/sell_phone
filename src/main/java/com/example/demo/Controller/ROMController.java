package com.example.demo.Controller;

import com.example.demo.Entitys.RAM;
import com.example.demo.Entitys.ROM;
import com.example.demo.Repository.RAMRepository;
import com.example.demo.Repository.ROMRepository;
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
@RequestMapping("rom")
public class ROMController {

    @Autowired
    private ROMRepository romRepository;

    @GetMapping("/index")
    public String index(Model model,
                        @RequestParam("page") Optional<Integer> pageParam,
                        RedirectAttributes redirectAttributes)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<ROM> data = romRepository.findAll(pageable);
        model.addAttribute("data", data);
        if (redirectAttributes.containsAttribute("success")) {
            model.addAttribute("success", redirectAttributes.getAttribute("success"));
        }
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách rom");
        return "admin/ql_thuoc_tinh/rom/index";
    }

    @GetMapping("/add")
    public String add(Model model)
    {
        model.addAttribute("message_title1", "Danh sách rom");
        model.addAttribute("message_title2", "Thêm mới rom");
        return "admin/ql_thuoc_tinh/rom/add";
    }

    @PostMapping("/store")
    public String store(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (romRepository.findByTenRomIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(romRepository.findByTenRomIsLike(ten.trim()) != null){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            model.addAttribute("ten", ten);
            model.addAttribute("message_title1", "Danh sách rom");
            model.addAttribute("message_title2", "Thêm mới rom");
            return "admin/ql_thuoc_tinh/rom/add";
        }else {
            ROM rom = new ROM();
            rom.setTenRom(ten);
            rom.setTrangThai(1);
            romRepository.save(rom);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/rom/index";
        }
    }


    @GetMapping("search")
    public String search(@RequestParam("search") String ten, Model model, @RequestParam("page") Optional<Integer> pageParam)
    {
        int page = pageParam.orElse(0);
        Pageable pageable = PageRequest.of(page, 5);
        Page<ROM> findSearch = romRepository.search(ten, pageable);
        model.addAttribute("data", findSearch);
        model.addAttribute("checkSearch", true);
        model.addAttribute("message_title1", "Quản lý sản phẩm");
        model.addAttribute("message_title2", "Danh sách rom");
        return "admin/ql_thuoc_tinh/rom/index";
    }


    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") BigInteger id)
    {
        ROM rom = romRepository.getReferenceById(id);
        model.addAttribute("rom", rom);
        model.addAttribute("message_title1", "Danh sách rom");
        model.addAttribute("message_title2", "Cập nhật rom");
        return "admin/ql_thuoc_tinh/rom/update";
    }

    @PostMapping("/update/{id}")
    public String update(
            Model model,
            @PathVariable("id") BigInteger id,
            @RequestParam("ten") String ten,
            RedirectAttributes redirectAttributes)
    {

        ROM romOld = romRepository.getReferenceById(id);
        if (ten.trim().length() == 0 || (romRepository.findByTenRomIsLike(ten.trim()) != null && !ten.trim().equals(romOld.getTenRom()))) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
            }
            else if(romRepository.findByTenRomIsLike(ten.trim()) != null && !ten.trim().equals(romOld.getTenRom())){
                model.addAttribute("loi",  "Loi_Trung!");
            }
            romOld.setTenRom(ten);
            model.addAttribute("rom", romOld);
            model.addAttribute("message_title1", "Danh sách rom");
            model.addAttribute("message_title2", "Cập nhật rom");
            return "admin/ql_thuoc_tinh/rom/update";
        }else {
            romOld.setTenRom(ten);
            romRepository.save(romOld);
            redirectAttributes.addFlashAttribute("success", "Cập nhật thành công!");
            return "redirect:/rom/index";
        }
    }

    @PostMapping("/quick-store")
    public Object quickStore(@RequestParam("ten") String ten,
                        Model model,
                        RedirectAttributes redirectAttributes)
    {

        if (ten.trim().length() == 0 || (romRepository.findByTenRomIsLike(ten.trim()) != null)) {
            if(ten.trim().length() == 0){
                model.addAttribute("loi",  "Loi_Trong!");
                return ResponseEntity.ok(1);
            }
            else{
                model.addAttribute("loi",  "Loi_Trung!");
                model.addAttribute("ten", ten);
                return  ResponseEntity.ok(2);
            }
        }else {
            ROM rom = new ROM();
            rom.setTenRom(ten);
            rom.setTrangThai(1);
            romRepository.save(rom);
            redirectAttributes.addFlashAttribute("success", "Thêm thành công!");
            return "redirect:/rom/index";
        }
    }


}
