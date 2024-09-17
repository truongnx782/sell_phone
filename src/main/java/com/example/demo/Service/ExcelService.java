package com.example.demo.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelService {
    public ByteArrayInputStream generateExcel(List<String[]> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("DoanhThuNgay");

            // Tạo tiêu đề
            int rowIdx = 0;
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(data.get(0)[0]);

            // Hợp nhất các ô để tạo tiêu đề
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            // Định dạng tiêu đề
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);
            titleCell.setCellStyle(titleStyle);

            // Tạo các hàng dữ liệu
            for (int i = 1; i < data.size(); i++) {
                Row row = sheet.createRow(rowIdx++);
                for (int j = 0; j < data.get(i).length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data.get(i)[j]);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
