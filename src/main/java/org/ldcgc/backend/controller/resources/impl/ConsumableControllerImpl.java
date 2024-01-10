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
import java.text.SimpleDateFormat;
import java.util.Optional;

@RestController
public class ConsumableControllerImpl implements ConsumableController{
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
    public ResponseEntity<?> uploadExcel(MultipartFile file){
        return consumableService.uploadExcel(file);
    }
}
