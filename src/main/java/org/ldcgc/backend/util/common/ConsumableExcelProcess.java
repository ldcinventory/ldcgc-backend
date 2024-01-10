package org.ldcgc.backend.util.common;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.payload.dto.excel.ConsumableExcelDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConsumableExcelProcess {

    public static List<ConsumableExcelDto> excelProcess(MultipartFile file){
        List<ConsumableExcelDto> consumable = new ArrayList<>();
        try{
            InputStream stream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            sheet.forEach( row ->{
                if(row.getRowNum() != 0){
                    consumable.add(parseToConsumable(row));
                }
            });
        } catch(IOException e){
            throw new RuntimeException(e);
        }
        return consumable;
    }
    private static ConsumableExcelDto parseToConsumable(Row row){
        return ConsumableExcelDto.builder()
                .barcode(String.valueOf(row.getCell(0).getNumericCellValue()))
                .category(row.getCell(1).getStringCellValue())
                .brand(row.getCell(2).getStringCellValue())
                .name(row.getCell(3).getStringCellValue())
                .model(row.getCell(4).getStringCellValue())
                .description(row.getCell(5).getStringCellValue())
                .urlImages(row.getCell(6).getStringCellValue())
                .stock((int)row.getCell(7).getNumericCellValue())
                .minStock((int)row.getCell(8).getNumericCellValue())
                .stockType(row.getCell(9).getStringCellValue())
                .locationLvl2(row.getCell(10).getStringCellValue())
                .group(row.getCell(11).getStringCellValue())
                .build();
    }

}

