package org.ldcgc.backend.service.resources.consumable;

import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.util.common.EOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ConsumableService {

    ResponseEntity<?> getConsumable(Integer consumableId);
    ResponseEntity<?> createConsumable(ConsumableDto consumable);
    ResponseEntity<?> listConsumables(String barcode, String category, String brand, String name, String model, String description, Boolean hasStock, Integer pageIndex, Integer size, String sortField, EOrder order);
    ResponseEntity<?> listConsumablesLoose(String filterString, Boolean hasStock, Integer pageIndex, Integer size, String sortField, EOrder order);
    ResponseEntity<?> updateConsumable(ConsumableDto consumableDto, Integer consumableId);
    ResponseEntity<?> deleteConsumable(Integer consumableId);
    ResponseEntity<?> loadExcel(Integer groupId, MultipartFile file);

}
