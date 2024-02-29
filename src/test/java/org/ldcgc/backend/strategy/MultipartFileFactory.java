package org.ldcgc.backend.strategy;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.common.EExcelToolsPositions;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.exceptions.PodamMockeryException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MultipartFileFactory {

    public static MultipartFile getFileFromTools(List<ToolDto> toolsExcel, EExcelToolsPositions wrongPosition) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hoja1");
            sheet.createRow(0);
            for (int i = 0; i < toolsExcel.size(); i++) {
                Row row = sheet.createRow(i + 1);
                ToolDto tool = toolsExcel.get(i);
                row.createCell(EExcelToolsPositions.BARCODE.getColumnNumber()).setCellValue(tool.getBarcode());
                row.createCell(EExcelToolsPositions.NAME.getColumnNumber()).setCellValue(tool.getName());
                row.createCell(EExcelToolsPositions.BRAND.getColumnNumber()).setCellValue(tool.getBrand().getName());
                row.createCell(EExcelToolsPositions.MODEL.getColumnNumber()).setCellValue(tool.getModel());
                row.createCell(EExcelToolsPositions.CATEGORY.getColumnNumber()).setCellValue(tool.getCategory().getName());
                row.createCell(EExcelToolsPositions.DESCRIPTION.getColumnNumber()).setCellValue(tool.getDescription());
                row.createCell(EExcelToolsPositions.URL_IMAGES.getColumnNumber()).setCellValue(String.join(", ", tool.getUrlImages()));
                row.createCell(EExcelToolsPositions.STATUS.getColumnNumber()).setCellValue(tool.getStatus().getDesc());
                row.createCell(EExcelToolsPositions.LOCATION.getColumnNumber()).setCellValue(tool.getLocation().getName());
                row.createCell(EExcelToolsPositions.MAINTENANCE_PERIOD.getColumnNumber()).setCellValue(tool.getMaintenancePeriod());
                row.createCell(EExcelToolsPositions.MAINTENANCE_TIME.getColumnNumber()).setCellValue(tool.getMaintenanceTime().getDesc());
                row.createCell(EExcelToolsPositions.LAST_MAINTENANCE.getColumnNumber()).setCellValue(tool.getLastMaintenance());
                row.createCell(EExcelToolsPositions.GROUP.getColumnNumber()).setCellValue(tool.getGroup().getName());

            }
            if(Objects.nonNull(wrongPosition)) {
                switch (wrongPosition) {
                    case BARCODE ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.BARCODE.getColumnNumber())).ifPresent(cell -> cell.setCellValue(123456789.0)));
                    case NAME ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.NAME.getColumnNumber())).ifPresent(cell -> cell.setCellValue(123456789.0)));
                    case BRAND ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.BRAND.getColumnNumber())).ifPresent(cell -> cell.setCellValue("made up brand")));
                    case MODEL ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.MODEL.getColumnNumber())).ifPresent(cell -> cell.setCellValue(123456789.0)));
                    case CATEGORY ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.CATEGORY.getColumnNumber())).ifPresent(cell -> cell.setCellValue("made up category")));
                    case DESCRIPTION ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.DESCRIPTION.getColumnNumber())).ifPresent(cell -> cell.setCellValue(123456789.0)));
                    case URL_IMAGES ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.URL_IMAGES.getColumnNumber())).ifPresent(cell -> cell.setCellValue(123456789.0)));
                    case STATUS ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.STATUS.getColumnNumber())).ifPresent(cell -> cell.setCellValue("made up status")));
                    case LOCATION ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.LOCATION.getColumnNumber())).ifPresent(cell -> cell.setCellValue("made up location")));
                    case MAINTENANCE_PERIOD ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.MAINTENANCE_PERIOD.getColumnNumber())).ifPresent(cell -> cell.setCellValue("made up maintenance period")));
                    case MAINTENANCE_TIME ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.MAINTENANCE_TIME.getColumnNumber())).ifPresent(cell -> cell.setCellValue(123456789.0)));
                    case LAST_MAINTENANCE ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.LAST_MAINTENANCE.getColumnNumber())).ifPresent(cell -> cell.setCellValue("made up date")));
                    case GROUP ->
                            sheet.rowIterator().forEachRemaining(row -> Optional.ofNullable(row.getCell(EExcelToolsPositions.GROUP.getColumnNumber())).ifPresent(cell -> cell.setCellValue("made up group")));
                }
            }

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                workbook.write(bos);
                byte[] bytes = bos.toByteArray();

                return new MockMultipartFile(
                    "excel.xlsx",
                    "excel.xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    bytes
                );
            } catch (Exception e) {
                throw new PodamMockeryException("Error creating MultipartFile", e);
            }
        }
    }
}
