package org.ldcgc.backend.strategy;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.common.EExcelPositions;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.exceptions.PodamMockeryException;

import java.io.IOException;
import java.util.List;

public class MultipartFileFactory {

    public static MultipartFile getFileFromTools(List<ToolDto> toolsExcel) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hoja1");
            sheet.createRow(0);
            for (int i = 0; i < toolsExcel.size(); i++) {
                Row row = sheet.createRow(i + 1);
                ToolDto tool = toolsExcel.get(i);
                row.createCell(EExcelPositions.BARCODE.getColumnNumber()).setCellValue(tool.getBarcode());
                row.createCell(EExcelPositions.NAME.getColumnNumber()).setCellValue(tool.getName());
                row.createCell(EExcelPositions.BRAND.getColumnNumber()).setCellValue(tool.getBrand().getName());
                row.createCell(EExcelPositions.MODEL.getColumnNumber()).setCellValue(tool.getModel());
                row.createCell(EExcelPositions.CATEGORY.getColumnNumber()).setCellValue(tool.getCategory().getName());
                row.createCell(EExcelPositions.DESCRIPTION.getColumnNumber()).setCellValue(tool.getDescription());
                row.createCell(EExcelPositions.URL_IMAGES.getColumnNumber()).setCellValue(tool.getUrlImages());
                row.createCell(EExcelPositions.STATUS.getColumnNumber()).setCellValue(tool.getStatus().getDesc());
                row.createCell(EExcelPositions.LOCATION.getColumnNumber()).setCellValue(tool.getLocation().getName());
                row.createCell(EExcelPositions.MAINTENANCE_PERIOD.getColumnNumber()).setCellValue(tool.getMaintenancePeriod());
                row.createCell(EExcelPositions.MAINTENANCE_TIME.getColumnNumber()).setCellValue(tool.getMaintenanceTime().getDesc());
                row.createCell(EExcelPositions.LAST_MAINTENANCE.getColumnNumber()).setCellValue(tool.getLastMaintenance());
                row.createCell(EExcelPositions.GROUP.getColumnNumber()).setCellValue(tool.getGroup().getName());

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
    public static MultipartFile getFileFromToolsIncorrectBarcodeType(List<ToolDto> toolsExcel) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hoja1");
            sheet.createRow(0);
            for (int i = 0; i < toolsExcel.size(); i++) {
                Row row = sheet.createRow(i + 1);
                ToolDto tool = toolsExcel.get(i);
                row.createCell(EExcelPositions.BARCODE.getColumnNumber()).setCellValue(123456789.0);
                row.createCell(EExcelPositions.NAME.getColumnNumber()).setCellValue(tool.getName());
                row.createCell(EExcelPositions.BRAND.getColumnNumber()).setCellValue(tool.getBrand().getName());
                row.createCell(EExcelPositions.MODEL.getColumnNumber()).setCellValue(tool.getModel());
                row.createCell(EExcelPositions.CATEGORY.getColumnNumber()).setCellValue(tool.getCategory().getName());
                row.createCell(EExcelPositions.DESCRIPTION.getColumnNumber()).setCellValue(tool.getDescription());
                row.createCell(EExcelPositions.URL_IMAGES.getColumnNumber()).setCellValue(tool.getUrlImages());
                row.createCell(EExcelPositions.STATUS.getColumnNumber()).setCellValue(tool.getStatus().getDesc());
                row.createCell(EExcelPositions.LOCATION.getColumnNumber()).setCellValue(tool.getLocation().getName());
                row.createCell(EExcelPositions.MAINTENANCE_PERIOD.getColumnNumber()).setCellValue(tool.getMaintenancePeriod());
                row.createCell(EExcelPositions.MAINTENANCE_TIME.getColumnNumber()).setCellValue(tool.getMaintenanceTime().getDesc());
                row.createCell(EExcelPositions.LAST_MAINTENANCE.getColumnNumber()).setCellValue(tool.getLastMaintenance());
                row.createCell(EExcelPositions.GROUP.getColumnNumber()).setCellValue(tool.getGroup().getName());

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
    public static MultipartFile getFileFromToolsIncorrectBrandType(List<ToolDto> toolsExcel) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hoja1");
            sheet.createRow(0);
            for (int i = 0; i < toolsExcel.size(); i++) {
                Row row = sheet.createRow(i + 1);
                ToolDto tool = toolsExcel.get(i);
                row.createCell(EExcelPositions.BARCODE.getColumnNumber()).setCellValue(tool.getBarcode());
                row.createCell(EExcelPositions.NAME.getColumnNumber()).setCellValue(tool.getName());
                row.createCell(EExcelPositions.BRAND.getColumnNumber()).setCellValue("made up brand");
                row.createCell(EExcelPositions.MODEL.getColumnNumber()).setCellValue(tool.getModel());
                row.createCell(EExcelPositions.CATEGORY.getColumnNumber()).setCellValue(tool.getCategory().getName());
                row.createCell(EExcelPositions.DESCRIPTION.getColumnNumber()).setCellValue(tool.getDescription());
                row.createCell(EExcelPositions.URL_IMAGES.getColumnNumber()).setCellValue(tool.getUrlImages());
                row.createCell(EExcelPositions.STATUS.getColumnNumber()).setCellValue(tool.getStatus().getDesc());
                row.createCell(EExcelPositions.LOCATION.getColumnNumber()).setCellValue(tool.getLocation().getName());
                row.createCell(EExcelPositions.MAINTENANCE_PERIOD.getColumnNumber()).setCellValue(tool.getMaintenancePeriod());
                row.createCell(EExcelPositions.MAINTENANCE_TIME.getColumnNumber()).setCellValue(tool.getMaintenanceTime().getDesc());
                row.createCell(EExcelPositions.LAST_MAINTENANCE.getColumnNumber()).setCellValue(tool.getLastMaintenance());
                row.createCell(EExcelPositions.GROUP.getColumnNumber()).setCellValue(tool.getGroup().getName());

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
