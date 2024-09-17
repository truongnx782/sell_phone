package com.example.demo.Controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class UploadsController {
    @Autowired
    Cloudinary cloudinary;
    @GetMapping("form")
    private String test1(){
        return "/nhan-vien/test";
    }
    @PostMapping("uploads")
    private String test(@RequestParam("avatar") MultipartFile file, Model model) throws IOException {
        String img="";
            Map r = this.cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            img=(String) r.get("secure_url");
        System.out.println(img);
        model.addAttribute("img",img);
        return "/nhan-vien/test";
    }
}
