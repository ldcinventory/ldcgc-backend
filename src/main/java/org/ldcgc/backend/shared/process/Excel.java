package org.ldcgc.backend.shared.process;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.app.exception.RequestException;
import org.ldcgc.backend.shared.constants.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.ldcgc.backend.shared.conversion.ExcelFunctions.getLastRowByColumn;

public class Excel {

    public static int getExcelResourcesAmount(MultipartFile excel, String sheetName, int initialRow, int column) {
        try {
            Workbook workbook = new XSSFWorkbook(excel.getInputStream());
            Sheet sheet = workbook.getSheet(sheetName);
            return getLastRowByColumn(sheet, column) - initialRow + 1;
        } catch (IOException e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY, Messages.Error.EXCEL_PARSE_ERROR);
        }
    }
}
