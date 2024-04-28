package org.ldcgc.backend.util.conversion;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.util.constants.Messages;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.ERROR;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import static org.ldcgc.backend.util.conversion.Convert.dateToLocalDate;
import static org.ldcgc.backend.util.conversion.Convert.stringToLocalDate;

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

        if (cellType.equals(ERROR))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber),
                    String.join(", ", new String[]{STRING.name(), FORMULA.name(), BLANK.name()})));

        try {
            if (cellType.equals(FORMULA))
                checkCellFormula(cell);

            if (excelCellNotValid("", cellType))
                return ((XSSFCell) cell).getRawValue();
        } catch (Exception e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber),
                    String.join(", ", new String[]{STRING.name(), FORMULA.name(), BLANK.name()})));
        }

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
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber),
                    String.join(", ", new String[]{NUMERIC.name(), STRING.name(), FORMULA.name()})));
        try {
            if (cellType.equals(FORMULA))
                checkCellFormula(cell);

            if (cellType.equals(STRING))
                return Integer.valueOf(cell.getStringCellValue());
        } catch (Exception e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber),
                    String.join(", ", new String[]{NUMERIC.name(), STRING.name(), FORMULA.name()})));
        }

        return (int) cell.getNumericCellValue();
    }

    public static Float getFloatCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (excelCellNotValid(0.0f, cellType))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber),
                    String.join(", ", new String[]{NUMERIC.name(), STRING.name(), FORMULA.name()})));

        try {
            if (cellType.equals(FORMULA))
                checkCellFormula(cell);

            if (cellType.equals(STRING))
                return Float.parseFloat(cell.getStringCellValue());
        } catch (Exception e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber),
                    String.join(", ", new String[]{NUMERIC.name(), STRING.name(), FORMULA.name()})));
        }

        return (float) cell.getNumericCellValue();
    }

    public static LocalDate getDateCellValue(Row row, Integer columnNumber) {
        Cell cell = row.getCell(columnNumber);
        CellType cellType = cell.getCellType();

        if (StringUtils.isBlank(cell.getStringCellValue())) return null;

        if (excelCellNotValid(LocalDate.now(), cellType))
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber),
                    String.join(", ", new String[]{STRING.name(), FORMULA.name(), BLANK.name()})));

        try {
            if (cellType.equals(FORMULA))
                checkCellFormula(cell);

            if (cellType.equals(STRING))
                return stringToLocalDate(cell.getStringCellValue(), "yyyy-MM-dd");
        } catch (Exception e) {
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(row.getRowNum(), columnNumber, getExcelAlphabetColumn(columnNumber),
                    String.join(", ", new String[]{STRING.name(), FORMULA.name(), BLANK.name()})));
        }

        return dateToLocalDate(cell.getDateCellValue());
    }

    private static boolean excelCellNotValid(Object cellClass, CellType cellType) {
        return !switch (cellClass) {
            case String ignored -> compareObjects(cellType, STRING, FORMULA, BLANK);
            case Integer ignored -> compareObjects(cellType, NUMERIC, STRING, FORMULA);
            case Float ignored -> compareObjects(cellType, NUMERIC, STRING, FORMULA);
            case LocalDate ignored -> compareObjects(cellType, STRING, FORMULA, BLANK);
            default -> false;
        };
    }

    private static boolean compareObjects(Object objectComparing, Object... objects) {
        for (Object o : objects)
            if (objectComparing == o) return true;
        return false;
    }

    private static void checkCellFormula(Cell cell) {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("Sheet1");
        Row row = sheet.createRow(0);
        Cell checkCell = row.createCell(0, cell.getCellType());
        checkCell.setCellFormula(cell.getCellFormula());

        FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        if(evaluator.evaluate(checkCell).getCellType().equals(ERROR))
            throw new RequestException(Messages.Error.EXCEL_CELL_TYPE_ERROR.formatted(
                cell.getRowIndex() + 1, cell.getColumnIndex() + 1, getExcelAlphabetColumn(cell.getColumnIndex())));
    }
}
