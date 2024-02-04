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
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.consumable.ConsumableExcelService;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
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
@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumableServiceImpl implements ConsumableService {

    //@Autowired
    private final ConsumableRepository consumableRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final GroupsService groupsService;
    private final ConsumableExcelService consumableExcelService;

    @Override
    public ResponseEntity<?> getConsumable(Integer consumableId) {
        Consumable consumable = consumableRepository.findById(consumableId).orElseThrow(() ->
                new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CONSUMABLE_NOT_FOUND, consumableId)));
        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableMapper.MAPPER.toDto(consumable));
    }

    @Override
    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        Optional<Consumable> repeatedConsumable = consumableRepository.findFirstByBarcode(consumable.getBarcode());
        if(repeatedConsumable.isPresent()){
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumable.getBarcode()));
        }
        try {
           Consumable consumableEntity = consumableRepository.saveAndFlush(ConsumableMapper.MAPPER.toMo(consumable));
            return Constructor.buildResponseObject(HttpStatus.OK, ConsumableMapper.MAPPER.toDto(consumableEntity));
        } catch(Exception e){
            return Constructor.buildResponseMessage(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> listConsumables(Integer pageIndex, Integer sizeIndex, String sortField, String filterString) {

        Pageable pageable = PageRequest.of(pageIndex, sizeIndex, Sort.by(sortField));

        try{

            Page<ConsumableDto> consumablePaged = consumableRepository.findByNameContainingOrDescriptionContaining(filterString, filterString, pageable)
                        .map(ConsumableMapper.MAPPER::toDto);

            return Constructor.buildResponseMessageObject(
                    HttpStatus.OK,
                    String.format(Messages.Info.CONSUMABLE_LISTED, consumablePaged.getTotalElements()),
                    consumablePaged);

        } catch (Exception e){
            System.out.println(e.getMessage());
            return Constructor.buildResponseMessage(HttpStatus.UNPROCESSABLE_ENTITY, "Error");
        }
    }

    @Override
    public ResponseEntity<?> updateConsumable(ConsumableDto consumableDto) {

        Optional<Consumable> repeatedConsumable = consumableRepository.findFirstByBarcode(consumableDto.getBarcode());
        if(repeatedConsumable.isPresent()){
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumableDto.getBarcode()));
        }
        try{
            consumableRepository.saveAndFlush(ConsumableMapper.MAPPER.toMo(consumableDto));
            return Constructor.buildResponseObject(HttpStatus.OK, consumableDto);
        } catch(Exception e){
            return Constructor.buildResponseMessage(HttpStatus.NOT_MODIFIED, e.getMessage());
        }
    }

    @Override
    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        Optional<Consumable> consumable = consumableRepository.findById(consumableId);
        if(consumable.isEmpty()){
            return Constructor.buildResponseMessage(HttpStatus.OK, String.format(Messages.Error.CONSUMABLE_NOT_FOUND, consumableId));
        }
        try{
            consumableRepository.deleteById(consumableId);
            return Constructor.buildResponseMessage(HttpStatus.OK, "Record deleted");
        } catch(Exception e){
            return Constructor.buildResponseMessage(HttpStatus.UNPROCESSABLE_ENTITY, "Error, record not deleted. ");
        }

    }

    @Override
    public ResponseEntity<?> loadExcel(MultipartFile file) {
        List<ConsumableDto> consumableToSave = consumableExcelService.excelToConsumable(file);

        consumableRepository.saveAll(consumableToSave.stream().map(ConsumableMapper.MAPPER::toMo).toList());

        return Constructor.buildResponseMessageObject(
                HttpStatus.OK,
                String.format(Messages.Info.TOOL_UPLOADED, consumableToSave.size()),
                consumableToSave);
    }

}
