package org.ldcgc.backend.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.common.EXlsxConsumablePos;
import org.ldcgc.backend.util.common.EXlsxToolPos;
import org.mockito.exceptions.base.MockitoException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
public class MultipartFileFactory {

    private final static String TOOLS_EXCEL = "tools.xlsx";
    private final static String CONSUMABLES_EXCEL = "consumables.xlsx";
    private final static String CONTENT_TYPE_EXCEL = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static MockMultipartFile getXLSXFromTools(List<ToolDto> toolsDto, EXlsxToolPos wrongPosition) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Tools");

            // head row
            Row row = sheet.createRow(0);
            for(EXlsxToolPos toolXY : EXlsxToolPos.values())
                row.createCell(toolXY.getColumnNumber()).setCellValue(toolXY.name());

            for (ToolDto toolDto : toolsDto) {
                row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(EXlsxToolPos.BARCODE.getColumnNumber()).setCellValue(toolDto.getBarcode());
                row.createCell(EXlsxToolPos.RESOURCE_TYPE.getColumnNumber()).setCellValue(toolDto.getResourceType().getName());
                row.createCell(EXlsxToolPos.BRAND.getColumnNumber()).setCellValue(toolDto.getBrand().getName());
                row.createCell(EXlsxToolPos.NAME.getColumnNumber()).setCellValue(toolDto.getName());
                row.createCell(EXlsxToolPos.MODEL.getColumnNumber()).setCellValue(toolDto.getModel());
                row.createCell(EXlsxToolPos.DESCRIPTION.getColumnNumber()).setCellValue(toolDto.getDescription());
                row.createCell(EXlsxToolPos.WEIGHT.getColumnNumber()).setCellValue(toolDto.getWeight());
                row.createCell(EXlsxToolPos.STOCK_WEIGHT_TYPE.getColumnNumber()).setCellValue(toolDto.getStockWeightType().name());
                row.createCell(EXlsxToolPos.PRICE.getColumnNumber()).setCellValue(toolDto.getPrice());
                row.createCell(EXlsxToolPos.PURCHASE_DATE.getColumnNumber()).setCellValue(toolDto.getPurchaseDate());
                row.createCell(EXlsxToolPos.MAINTENANCE_FREQUENCY.getColumnNumber()).setCellValue(toolDto.getMaintenancePeriod());
                row.createCell(EXlsxToolPos.MAINTENANCE_PERIOD.getColumnNumber()).setCellValue(toolDto.getMaintenanceTime().getName());
                row.createCell(EXlsxToolPos.LAST_MAINTENANCE.getColumnNumber()).setCellValue(toolDto.getLastMaintenance());
                row.createCell(EXlsxToolPos.NEXT_MAINTENANCE.getColumnNumber()).setCellValue(toolDto.getNextMaintenance());
                row.createCell(EXlsxToolPos.STATUS.getColumnNumber()).setCellValue(toolDto.getStatus().getDesc());
                row.createCell(EXlsxToolPos.LOCATION.getColumnNumber()).setCellValue(toolDto.getLocation().getName());
            }

            switch (wrongPosition) {
                case BARCODE, NAME, MODEL, DESCRIPTION, WEIGHT, PRICE, MAINTENANCE_PERIOD ->
                    sheet.rowIterator().forEachRemaining(r -> Optional.ofNullable(r.getCell(wrongPosition.getColumnNumber()))
                        .ifPresent(c -> c.setCellFormula("1/0")));
                case RESOURCE_TYPE, BRAND, STOCK_WEIGHT_TYPE, PURCHASE_DATE, MAINTENANCE_FREQUENCY, LAST_MAINTENANCE, NEXT_MAINTENANCE, STATUS, LOCATION ->
                    sheet.rowIterator().forEachRemaining(r -> Optional.ofNullable(r.getCell(wrongPosition.getColumnNumber()))
                        .ifPresent(c -> c.setCellValue("mocked " + wrongPosition.name().toLowerCase())));
                case null -> log.info("No wrong position");
            }

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
                 FileOutputStream outputStream = new FileOutputStream(TOOLS_EXCEL)) {
                // write excel on root folder
                workbook.write(outputStream);

                workbook.write(bos);
                byte[] bytes = bos.toByteArray();

                return new MockMultipartFile(TOOLS_EXCEL, TOOLS_EXCEL, CONTENT_TYPE_EXCEL, bytes);
            } catch (Exception e) {
                throw new MockitoException("Error creating MultipartFile", e);
            }
        }
    }

    public static MultipartFile getXLSXFromConsumables(List<ConsumableDto> consumablesDto, EXlsxConsumablePos wrongPosition) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Consumables");

            // head row
            Row row = sheet.createRow(0);
            for(EXlsxConsumablePos consumablesXY : EXlsxConsumablePos.values())
                row.createCell(consumablesXY.getColumnNumber()).setCellValue(consumablesXY.name());

            for (ConsumableDto consumableDto : consumablesDto) {
                row = sheet.createRow(sheet.getLastRowNum() + 1);
                row.createCell(EXlsxConsumablePos.BARCODE.getColumnNumber()).setCellValue(consumableDto.getBarcode());
                row.createCell(EXlsxConsumablePos.RESOURCE_TYPE.getColumnNumber()).setCellValue(consumableDto.getResourceType().getName());
                row.createCell(EXlsxConsumablePos.BRAND.getColumnNumber()).setCellValue(consumableDto.getBrand().getName());
                row.createCell(EXlsxConsumablePos.NAME.getColumnNumber()).setCellValue(consumableDto.getName());
                row.createCell(EXlsxConsumablePos.MODEL.getColumnNumber()).setCellValue(consumableDto.getModel());
                row.createCell(EXlsxConsumablePos.DESCRIPTION.getColumnNumber()).setCellValue(consumableDto.getDescription());
                row.createCell(EXlsxConsumablePos.PRICE.getColumnNumber()).setCellValue(consumableDto.getPrice());
                row.createCell(EXlsxConsumablePos.PURCHASE_DATE.getColumnNumber()).setCellValue(consumableDto.getPurchaseDate());
                row.createCell(EXlsxConsumablePos.QTY_EACH_ITEM.getColumnNumber()).setCellValue(consumableDto.getQuantityEachItem());
                row.createCell(EXlsxConsumablePos.STOCK.getColumnNumber()).setCellValue(consumableDto.getStock());
                row.createCell(EXlsxConsumablePos.MIN_STOCK.getColumnNumber()).setCellValue(consumableDto.getMinStock());
                row.createCell(EXlsxConsumablePos.STOCK_TYPE.getColumnNumber()).setCellValue(consumableDto.getStockType().name());
                row.createCell(EXlsxConsumablePos.LOCATION.getColumnNumber()).setCellValue(consumableDto.getLocation().getName());
                row.createCell(EXlsxConsumablePos.GROUP.getColumnNumber()).setCellValue(consumableDto.getGroup().getName());
            }

            switch (wrongPosition) {
                case BARCODE, NAME, MODEL, DESCRIPTION ->
                    sheet.rowIterator().forEachRemaining(r -> Optional.ofNullable(r.getCell(wrongPosition.getColumnNumber()))
                        .ifPresent(c -> c.setCellValue(RandomStringUtils.randomAlphanumeric(8))));
                case MIN_STOCK, PRICE, STOCK, QTY_EACH_ITEM ->
                    sheet.rowIterator().forEachRemaining(r -> Optional.ofNullable(r.getCell(wrongPosition.getColumnNumber()))
                        .ifPresent(c -> c.setCellValue(Float.parseFloat(RandomStringUtils.randomNumeric(3)) / 100.00f)));
                case BRAND, RESOURCE_TYPE, LOCATION, GROUP, PURCHASE_DATE, STOCK_TYPE ->
                    sheet.rowIterator().forEachRemaining(r -> Optional.ofNullable(r.getCell(wrongPosition.getColumnNumber()))
                        .ifPresent(c -> c.setCellValue("mocked " + wrongPosition.name().toLowerCase())));
            }

            try (ByteArrayOutputStream bos = new ByteArrayOutputStream() ;
                 FileOutputStream outputStream = new FileOutputStream(CONSUMABLES_EXCEL)) {
                // download excel
                workbook.write(outputStream);

                workbook.write(bos);
                byte[] bytes = bos.toByteArray();

                return new MockMultipartFile(CONSUMABLES_EXCEL, CONSUMABLES_EXCEL, CONTENT_TYPE_EXCEL, bytes);
            } catch (Exception e) {
                throw new MockitoException("Error creating MultipartFile", e);
            }
        }
    }
}
