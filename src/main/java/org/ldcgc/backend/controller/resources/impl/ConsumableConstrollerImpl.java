package org.ldcgc.backend.controller.resources.impl;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.controller.resources.ConsumableController;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class ConsumableConstrollerImpl implements ConsumableController {

    @Autowired
    private ConsumableService consumableService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public ResponseEntity<?> testAccessWithCredentials() {
        return null;
    }

    @Override
    public ResponseEntity<?> testAccessWithAdminCredentials() {
        return null;
    }

    public ResponseEntity<?> getConsumable(Integer consumableId) {
        return consumableService.getConsumable(consumableId);
    }

    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        return consumableService.createConsumable(consumable);
    }

    @Override
    public ResponseEntity<?> updateConsumable(ConsumableDto consumable) {
        return consumableService.updateConsumable(consumable);
    }

    @Override
    public ResponseEntity<?> listConsumables(Integer pageIndex, Integer sizeIndex, String filterString) {
        return consumableService.listConsumables(pageIndex, sizeIndex, filterString);
    }

    @Override
    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        return consumableService.deleteConsumable(consumableId);
    }

    @Override
    public ResponseEntity<?> uploadExcelFile(MultipartFile file) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            int maxRows = sheet.getLastRowNum();
            System.out.printf("Numero total de filas %s", maxRows);
            System.out.println();
            for(int i = 1; i < maxRows; i++){

                String barcode  = "";
                if(sheet.getRow(i).getCell(0) != null){
                    barcode = sheet.getRow(i).getCell(0).toString();
                }

                String name = "";
                if(sheet.getRow(i).getCell(1) != null) {
                    name = sheet.getRow(i).getCell(1).toString();
                }
                String description = "";
                if(sheet.getRow(i).getCell(2) != null){
                    description = sheet.getRow(i).getCell(2).toString();
                }

                String model = "";
                if(sheet.getRow(i).getCell(3) != null) {
                    model = sheet.getRow(i).getCell(3).toString();
                }
                String stock = "";
                if(sheet.getRow(i).getCell(4) != null){
                    stock = sheet.getRow(i).getCell(4).toString();
                }
                String minStock = "";
                if(sheet.getRow(i).getCell(5) != null){
                    minStock  = sheet.getRow(i).getCell(5).toString();
                }

                String price = "";
                if(sheet.getRow(i).getCell(6) != null) {
                    price = sheet.getRow(i).getCell(6).toString();
                }

                String locationId = "";
                if(sheet.getRow(i).getCell(7) != null){
                    locationId = sheet.getRow(i).getCell(7).toString();

                }

                String categoryName = "";
                Integer categoryId = null;
                if(sheet.getRow(i).getCell(8) != null){
                    categoryName = sheet.getRow(i).getCell(8).toString();
                    categoryId = Optional.ofNullable(categoryService.getCategoryIdByName(categoryName)).orElse(0);
                }

                String brandName  = "";
                if(sheet.getRow(i).getCell(9) != null){
                    brandName = sheet.getRow(i).getCell(9).toString();
                }

                String purchaseDate = "";
                if(sheet.getRow(i).getCell(10) != null){
                    purchaseDate = sheet.getRow(i).getCell(10).getStringCellValue();
                }

                System.out.printf("Nombre: %s\n Description: %s\n Barcode: %s\n Model: %s\n MinStock: %s\n Price: %s\n Stock: %s\n PurchasedDate: %s\n Category Name: %s\n CategoryId: %d\n Brand Name: %s\n", name, description, barcode, model, minStock, price, stock, purchaseDate, categoryName, categoryId, brandName);
                System.out.println("##################\n");


            }

            workbook.close();
        } catch (IOException e) {
            // Maneja las excepciones
            System.out.println(e.getMessage());
        }

        return null;
    }


}
