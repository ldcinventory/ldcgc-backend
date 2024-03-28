package org.ldcgc.backend.util.conversion;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import static org.ldcgc.backend.util.conversion.Convert.dateToLocalDate;
import static org.ldcgc.backend.util.conversion.Convert.stringToLocalDate;
import static org.ldcgc.backend.util.conversion.Convert.toFloat2Decimals;

public class ExcelFunctions {

    private static String[] excelColumns;

    public static String getExcelAlphabetColumn(Integer columnNumber) {
        return excelColumns[columnNumber];
    }

    public static void processExcelArray() {
        if(excelColumns == null) {
            excelColumns = new String[26 * 26];

            int index = 0;
            for (int i = 0; i <= 2; i++) {
                char firstChar = (char) ('A' + i);
                for (int j = 0; j < 26; j++) {
                    char secondChar = (char) ('A' + j);
                    if (i == 0)
                        excelColumns[index] = String.valueOf(secondChar);
                    else
                        excelColumns[index] = firstChar + String.valueOf(secondChar);
                    index++;
                }
            }
        }
    }

    public static String getStringCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (excelCellNotValid("", cellType))
            return ((XSSFCell) cell).getRawValue();

        return cell.getStringCellValue();
    }

    public static String[] getStringArrayCellValue(Row row, Integer columnNumber) {
        String cellValue = getStringCellValue(row, columnNumber);
        return cellValue.split(", ?");
    }

    public static Integer getIntegerCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (excelCellNotValid(0, cellType))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber), NUMERIC.toString()));

        if (cellType.equals(STRING))
            return Integer.valueOf(cell.getStringCellValue());

        return (int) cell.getNumericCellValue();
    }

    public static Float getFloatCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();
        if (excelCellNotValid(0.0f, cellType))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber), STRING.toString()));
        if (cellType.equals(STRING))
            return Float.parseFloat(cell.getStringCellValue());

        return (float) cell.getNumericCellValue();
    }

    public static LocalDate getDateCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (excelCellNotValid(LocalDate.now(), cellType))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber), STRING.toString()));
        if (cellType.equals(STRING))
            return stringToLocalDate(cell.getStringCellValue(), "yyyy-MM-dd");

        return dateToLocalDate(cell.getDateCellValue());
    }

    private static boolean excelCellNotValid(Object cellClass, CellType cellType) {
        return !switch (cellClass) {
            case String s -> compareObjects(cellType, STRING, FORMULA, BLANK);
            case Integer i -> compareObjects(cellType, NUMERIC, FORMULA);
            case Float f -> compareObjects(cellType, NUMERIC, STRING, FORMULA);
            case LocalDate l -> compareObjects(cellType, STRING, FORMULA);
            default -> false;
        };
    }

    private static boolean compareObjects(Object objectComparing, Object... objects) {
        for (Object o : objects)
            if (objectComparing == o) return true;
        return false;
    }
}
