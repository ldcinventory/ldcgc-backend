package org.ldcgc.backend.strategy;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.payload.dto.excel.ToolExcelDto;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.exceptions.PodamMockeryException;

import java.util.List;

public class MultipartFileFactory {
    //CABECERAS DEL EXCEL
    //"Código de barras", "Nombre", "Marca", "Modelo", "Categoría", "Descripción",	"Url Imágenes",
    //"Estado", "Localización", "Período de mantenimiento (numérico)", "Unidad periodo de mantenimiento", "Fecha último mantenimiento", "Grupo"
    private static final Integer rowLength = 13;

    public static MultipartFile getFileFromTools(List<ToolExcelDto> toolsExcel) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Hoja1");
        sheet.createRow(0);
        for (int i = 1; i < toolsExcel.size() + 1; i++){
            Row row = sheet.createRow(i);
            for(int j = 0; j < rowLength; j++){
                Cell cell = row.createCell(j);
                switch (j) {
                    case 0:
                        cell.setCellValue(toolsExcel.get(i-1).getBarcode());
                    case 1:
                        cell.setCellValue(toolsExcel.get(i-1).getName());
                    case 2:
                        cell.setCellValue(toolsExcel.get(i-1).getBrand());
                    case 3:
                        cell.setCellValue(toolsExcel.get(i-1).getModel());
                    case 4:
                        cell.setCellValue(toolsExcel.get(i-1).getCategory());
                    case 5:
                        cell.setCellValue(toolsExcel.get(i-1).getDescription());
                    case 6:
                        cell.setCellValue(toolsExcel.get(i-1).getUrlImages());
                    case 7:
                        cell.setCellValue(toolsExcel.get(i-1).getStatus());
                    case 8:
                        cell.setCellValue(toolsExcel.get(i-1).getLocation());
                    case 9:
                        cell.setCellValue(toolsExcel.get(i-1).getMaintenancePeriod().doubleValue());
                    case 10:
                        cell.setCellValue(toolsExcel.get(i-1).getMaintenanceTime());
                    case 11:
                        cell.setCellValue(toolsExcel.get(i-1).getLastMaintenance());
                    case 12:
                        cell.setCellValue(toolsExcel.get(i-1).getGroup());
                }
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
        }
        catch (Exception e){
            throw new PodamMockeryException("Error creating MultipartFile", e);
        }
    }
}
