package org.ldcgc.backend.service.resources.consumable.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.excel.ConsumableExcelDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.ldcgc.backend.util.common.ConsumableExcelProcess;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.CONSUMABLE_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.CONSUMABLE_LISTED;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumableServiceImpl implements ConsumableService {

    //@Autowired
    private final ConsumableRepository consumableRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final GroupsService groupsService;

    @Override
    public ResponseEntity<?> getConsumable(Integer consumableId) {
        Consumable consumable = consumableRepository.findById(consumableId).orElseThrow(() ->
                new RequestException(HttpStatus.NOT_FOUND, String.format(getErrorMessage(CONSUMABLE_NOT_FOUND), consumableId)));
        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableMapper.MAPPER.toDto(consumable));
    }

    @Override
    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        Consumable consumableEntity = ConsumableMapper.MAPPER.toMo(consumable);
        try {
            consumableRepository.saveAndFlush(consumableEntity);
            return Constructor.buildResponseObject(HttpStatus.OK, consumableEntity);
        } catch(Exception e){
            return Constructor.buildResponseMessage(HttpStatus.NOT_MODIFIED, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> listConsumables(Integer pageIndex, Integer sizeIndex, String filterString) {
        Map<String, Object> response = new HashMap<>();
        Page<Consumable> consumablePaged;
        List<ConsumableDto> consumableDto = new ArrayList<>();

        Pageable pageable = PageRequest.of(pageIndex, sizeIndex, Sort.by("id").descending());

        try{
            if(filterString == null){

                consumablePaged = consumableRepository.findAll(pageable);

            } else {

                consumablePaged = consumableRepository.findByNameContainingOrDescriptionContaining(filterString, filterString, pageable);

            }

            consumablePaged.forEach(consumable -> {
                //System.out.println("Consumable: " + consumable.getName() + " - " +consumable.getDescription());
                consumableDto.add(ConsumableMapper.MAPPER.toDto(consumable));
            });
            response.put("totalPages", consumablePaged.getTotalPages());
            response.put("totalRecords", consumablePaged.getTotalElements());
            response.put("consumables", consumableDto);

            return Constructor.buildResponseMessagePageable(HttpStatus.OK, String.format(getInfoMessage(CONSUMABLE_LISTED), consumablePaged.getTotalElements()), response);

        } catch (Exception e){
            System.out.println(e.getMessage());
            return Constructor.buildResponseMessage(HttpStatus.UNPROCESSABLE_ENTITY, "Error");
        }
    }

    @Override
    public ResponseEntity<?> updateConsumable(ConsumableDto consumableDto) {
        Consumable consumableEntity = ConsumableMapper.MAPPER.toMo(consumableDto);
        try{
            consumableRepository.saveAndFlush(consumableEntity);
            return Constructor.buildResponseObject(HttpStatus.OK, consumableEntity);
        } catch(Exception e){
            return Constructor.buildResponseMessage(HttpStatus.NOT_MODIFIED, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        Optional<Consumable> consumable = consumableRepository.findById(consumableId);
        if(consumable.isEmpty()){
            return Constructor.buildResponseMessage(HttpStatus.OK, String.format(getErrorMessage(CONSUMABLE_NOT_FOUND), consumableId));//String.format(getErrorMessage(CONSUMABLE_NOT_FOUND), consumableId)
        } else {
            try{
                consumableRepository.deleteById(consumableId);
                return Constructor.buildResponseMessage(HttpStatus.OK, "Record deleted");
            } catch(Exception e){
                System.out.println(e.getMessage());
                return Constructor.buildResponseMessage(HttpStatus.UNPROCESSABLE_ENTITY, "Error, record not deleted. ");
            }
        }

    }

    @Override
    public ResponseEntity<?> loadExcel(MultipartFile file) {

        List<ConsumableExcelDto> consumableExcel = ConsumableExcelProcess.excelProcess(file);

        List<ConsumableDto> consumableToSave = convertExcelToConsumable(consumableExcel);

        consumableRepository.saveAll(consumableToSave.stream().map(ConsumableMapper.MAPPER::toMo).toList());

        return null;
    }
    private List<ConsumableDto> convertExcelToConsumable(List<ConsumableExcelDto> consumablesExcel){
        List<ConsumableDto> consumable = consumableRepository.findByBarcodeIn(consumablesExcel.stream().map(ConsumableExcelDto::getBarcode).toList())
                .stream()
                .map(ConsumableMapper.MAPPER::toDto)
                .toList();
        CategoryDto brandParent = categoryService.getCategoryParent(CategoryParentEnum.BRANDS);
        CategoryDto categoryParent = categoryService.getCategoryParent(CategoryParentEnum.CATEGORIES);
        CategoryDto stockTypeParent = categoryService.getCategoryParent(CategoryParentEnum.STOCKTYPE);
        List<GroupDto> groups = groupsService.getAllGroups();
        List<LocationDto> locations = locationService.getAllLocations();

        return consumablesExcel.stream()
                .map(consumableExcel -> ConsumableDto.builder()
                        .id(getIdByBarcode(consumableExcel, consumable))
                        .barcode(consumableExcel.getBarcode())
                        .category(categoryService.getCategoryByName(consumableExcel.getCategory(), categoryParent))
                        .brand(categoryService.getCategoryByName(consumableExcel.getBrand(), brandParent))
                        .name(consumableExcel.getName())
                        .model(consumableExcel.getModel())
                        .description(consumableExcel.getDescription())
                        .urlImages(consumableExcel.getUrlImages())
                        .stock(consumableExcel.getStock())
                        .stockType(categoryService.getCategoryByName(consumableExcel.getStockType(), stockTypeParent))
                        .locationLvl2(locationService.findLocationByName(consumableExcel.getLocationLvl2(), locations))
                        .group(groupsService.findGroupInListByName(consumableExcel.getGroup(), groups))
                        .build()
                ).toList();
    }
    @Nullable
    private Integer getIdByBarcode(ConsumableExcelDto consumableExcel, List<ConsumableDto> consumable) {
        return consumable.stream()
                .filter(tool -> tool.getBarcode().equals(consumableExcel.getBarcode()))
                .map(ConsumableDto::getId)
                .findFirst()
                .orElse(null);
    }
}
