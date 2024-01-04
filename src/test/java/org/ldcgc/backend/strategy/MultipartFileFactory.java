package org.ldcgc.backend.strategy;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.exceptions.PodamMockeryException;

import java.io.IOException;
import java.util.List;

public class MultipartFileFactory {

    public static MultipartFile getFileFromTools(List<ToolExcelDto> toolsExcel) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Hoja1");
            sheet.createRow(0);
            for (int i = 0; i < toolsExcel.size(); i++) {
                Row row = sheet.createRow(i + 1);
                // CABECERAS DEL EXCEL
                // "Código de barras",
                row.createCell(0).setCellValue(toolsExcel.get(i).getBarcode());
                // "Nombre",
                row.createCell(1).setCellValue(toolsExcel.get(i).getName());
                // "Marca",
                row.createCell(2).setCellValue(toolsExcel.get(i).getBrand());
                // "Modelo",
                row.createCell(3).setCellValue(toolsExcel.get(i).getModel());
                // "Categoría",
                row.createCell(4).setCellValue(toolsExcel.get(i).getCategory());
                // "Descripción",
                row.createCell(5).setCellValue(toolsExcel.get(i).getDescription());
                // "Url Imágenes",
                row.createCell(6).setCellValue(toolsExcel.get(i).getUrlImages());
                // "Estado",
                row.createCell(7).setCellValue(toolsExcel.get(i).getStatus());
                // "Localización",
                row.createCell(8).setCellValue(toolsExcel.get(i).getLocation());
                // "Período de mantenimiento (numérico)",
                row.createCell(9).setCellValue(toolsExcel.get(i).getMaintenancePeriod());
                // "Unidad periodo de mantenimiento",
                row.createCell(10).setCellValue(toolsExcel.get(i).getMaintenanceTime());
                // "Fecha último mantenimiento",
                row.createCell(11).setCellValue(toolsExcel.get(i).getLastMaintenance());
                // "Grupo"
                row.createCell(12).setCellValue(toolsExcel.get(i).getGroup());

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
