package com.example.demo.Service;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;



@Service

public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    private final String postmarkApiKey = "6926d638-c1c2-4e95-a0ef-660acfeeeae4";

    public void sendEmail(String[] to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("nguyenxuantruongtest1@gmail.com");
        mailSender.send(message);
    }

//    private final TemplateEngine templateEngine;

//    public EmailService(TemplateEngine templateEngine) {
//        this.templateEngine = templateEngine;
//    }
//
    TemplateEngine templateEngine = new TemplateEngine();



//    Gửi file báo cáo excel qua mail
public void sendEmailWithExcel(String email, List<String[]> data) throws MessagingException, IOException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);

    helper.setTo(email);
    helper.setSubject("BÁO CÁO DOANH THU NGÀY");
    helper.setText("Tổng hợp báo cáo doanh thu ngày của MPShop");

    // Tạo tệp Excel
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try (Workbook workbook = new XSSFWorkbook()) {
        Sheet sheet = workbook.createSheet("Báo cáo doanh thu");

        // Tạo hàng tiêu đề
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < data.get(0).length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(data.get(0)[i]);
        }

        // Tạo các hàng dữ liệu
        for (int i = 1; i < data.size(); i++) {
            Row row = sheet.createRow(i);
            for (int j = 0; j < data.get(i).length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data.get(i)[j]);
            }
        }

        workbook.write(out);
    }

    // Đính kèm tệp Excel
    InputStreamSource attachment = new ByteArrayResource(out.toByteArray());
    String filename = "baocaodoanhthu_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".xlsx";
    helper.addAttachment(filename, attachment);

    mailSender.send(message);
}

}
