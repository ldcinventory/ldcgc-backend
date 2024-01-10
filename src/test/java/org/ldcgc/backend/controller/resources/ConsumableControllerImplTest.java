package org.ldcgc.backend.controller.resources;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class ConsumableControllerImplTest {
    @Mock
    private ConsumableService service;
    private CategoryService categoryService;

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
                Integer brandId = null;
                if(sheet.getRow(i).getCell(9) != null){
                    brandName = sheet.getRow(i).getCell(9).toString();
                    brandId = Optional.ofNullable(categoryService.getCategoryIdByName(brandName)).orElse(0);
                }

                String purchaseDate = "";
                if(sheet.getRow(i).getCell(10) != null){
                    purchaseDate = sheet.getRow(i).getCell(10).getStringCellValue();
                }

                System.out.printf("Nombre: %s\n Description: %s\n Barcode: %s\n Model: %s\n MinStock: %s\n Price: %s\n Stock: %s\n PurchasedDate: %s\n Category Name: %s\n CategoryId: %d\n Brand Name: %s\n BrandId: %d\n", name, description, barcode, model, minStock, price, stock, purchaseDate, categoryName, categoryId, brandName, brandId);
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
