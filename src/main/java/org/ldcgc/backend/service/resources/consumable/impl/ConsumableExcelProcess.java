package org.ldcgc.backend.service.resources.consumable.impl;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.excel.ConsumableExcelDto;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConsumableExcelProcess {

    public static List<ConsumableExcelDto> excelProcess(MultipartFile file) {
        AtomicInteger rowNum = new AtomicInteger();
        List<ConsumableExcelDto> consumable = new ArrayList<>();
        try {
            InputStream stream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(stream);
            Sheet sheet = workbook.getSheetAt(0);
            sheet.forEach(row -> {
                rowNum.getAndIncrement();
                if (row.getRowNum() != 0)
                    consumable.add(parseToConsumable(row));
            });
        } catch (IOException e) {
            // TODO controlar errores -> fila (ya hecho), columna, tipo válido
            String cellErrorMessage = String.format(Messages.Error.EXCEL_CELL_TYPE_INCORRECT, rowNum, "columna", "tipo válido");
            String errorMessage = String.format("%s %s", cellErrorMessage, Messages.Error.EXCEL_PARSE_ERROR);
            throw new RequestException(HttpStatus.PROCESSING, errorMessage);
        }
        return consumable;
    }

    private static ConsumableExcelDto parseToConsumable(Row row) {
        return ConsumableExcelDto.builder()
            .barcode(String.valueOf(row.getCell(0).getNumericCellValue()))
            .category(row.getCell(1).getStringCellValue())
            .brand(row.getCell(2).getStringCellValue())
            .name(row.getCell(3).getStringCellValue())
            .model(row.getCell(4).getStringCellValue())
            .description(row.getCell(5).getStringCellValue())
            .urlImages(row.getCell(6).getStringCellValue())
            .stock((int) row.getCell(7).getNumericCellValue())
            .minStock((int) row.getCell(8).getNumericCellValue())
            .stockType(row.getCell(9).getStringCellValue())
            .location(row.getCell(10).getStringCellValue())
            .group(row.getCell(11).getStringCellValue())
            .build();
    }

}

