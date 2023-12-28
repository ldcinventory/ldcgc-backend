package org.ldcgc.backend.util.common;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.payload.dto.excel.ToolExcelDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtils {

    public static List<ToolExcelDto> excelToTools(MultipartFile excel) {
        List<ToolExcelDto> tools = new ArrayList<>();

        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            sheet.forEach(row -> {
                if(row.getRowNum() != 0)
                    tools.add(parseRowToTool(row));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tools;
    }

    private static ToolExcelDto parseRowToTool(Row row) {
        return ToolExcelDto.builder()
                .barcode(row.getCell(0).getStringCellValue())
                .name(row.getCell(1).getStringCellValue())
                .brand(row.getCell(2).getStringCellValue())
                .model(row.getCell(3).getStringCellValue())
                .category(row.getCell(4).getStringCellValue())
                .description(row.getCell(5).getStringCellValue())
                .urlImages(row.getCell(6).getStringCellValue())
                .status(row.getCell(7).getStringCellValue())
                .location(row.getCell(8).getStringCellValue())
                .maintenancePeriod((int)row.getCell(9).getNumericCellValue())
                .maintenanceTime(row.getCell(10).getStringCellValue())
                .lastMaintenance(row.getCell(11).getLocalDateTimeCellValue())
                .group(row.getCell(12).getStringCellValue())
                .build();
    }

}
