package com.rtvnewsnetwork.excel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
//
//@RestController
//@RequestMapping("/api/excel")
//public class ExcelController {
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @GetMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<byte[]> uploadJson(@RequestParam("file") MultipartFile file) throws IOException {
//        // Parse JSON file into list of maps
//        List<Map<String, Object>> jsonData = mapper.readValue(
//                file.getInputStream(),
//                new TypeReference<>() {}
//        );
//
//        // Convert JSON -> Excel
//        byte[] excelFile = createExcelFromJson(jsonData);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.xlsx");
//        headers.setContentType(MediaType.parseMediaType(
//                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
//
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(excelFile);
//    }
//
//    private byte[] createExcelFromJson(List<Map<String, Object>> jsonData) throws IOException {
//        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
//            Sheet sheet = workbook.createSheet("Data");
//
//            if (jsonData.isEmpty()) return new byte[0];
//
//            // ✅ Use first object for headers
//            Map<String, Object> firstRow = jsonData.get(0);
//
//            // Create header row
//            Row headerRow = sheet.createRow(0);
//            int headerCol = 0;
//            for (String key : firstRow.keySet()) {
//                Cell cell = headerRow.createCell(headerCol++);
//                cell.setCellValue(key);
//                CellStyle style = workbook.createCellStyle();
//                Font font = workbook.createFont();
//                font.setBold(true);
//                style.setFont(font);
//                cell.setCellStyle(style);
//            }
//
//            // Fill data rows
//            int rowIdx = 1;
//            for (Map<String, Object> rowMap : jsonData) {
//                Row row = sheet.createRow(rowIdx++);
//                int colIdx = 0;
//                for (String key : firstRow.keySet()) {
//                    Cell cell = row.createCell(colIdx++);
//                    Object value = rowMap.get(key);
//                    cell.setCellValue(value != null ? value.toString() : "");
//                }
//            }
//
//            // Auto-size columns
//            for (int i = 0; i < firstRow.size(); i++) {
//                sheet.autoSizeColumn(i);
//            }
//
//            workbook.write(out);
//            return out.toByteArray();
//        }
//    }
//}