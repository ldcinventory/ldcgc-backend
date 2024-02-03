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

import java.util.List;
import java.util.Objects;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.CONSUMABLE_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.CONSUMABLE_LISTED;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumableServiceImpl implements ConsumableService {

    private final ConsumableRepository consumableRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final GroupsService groupsService;

    @Override
    public ResponseEntity<?> getConsumable(Integer consumableId) {
        Consumable consumable = getOrElseThrow(consumableId);
        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableMapper.MAPPER.toDto(consumable));
    }

    @Override
    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        if(Objects.nonNull(consumable.getId()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_ID_SHOULDNT_BE_PRESENT);

        consumableRepository.findFirstByBarcode(consumable.getBarcode())
                .ifPresent(repeated -> {
                    throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumable.getBarcode()));
                });

        Consumable consumableEntity = consumableRepository.saveAndFlush(ConsumableMapper.MAPPER.toMo(consumable));
        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableMapper.MAPPER.toDto(consumableEntity));

    }

    @Override
    public ResponseEntity<?> listConsumables(Integer pageIndex, Integer sizeIndex, String sortField, String filterString) {
        Pageable pageable = PageRequest.of(pageIndex, sizeIndex, Sort.by(sortField));

        Page<ConsumableDto> consumablePaged = consumableRepository.findByNameContainingOrDescriptionContaining(filterString, filterString, pageable)
                .map(ConsumableMapper.MAPPER::toDto);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, String.format(getInfoMessage(CONSUMABLE_LISTED), consumablePaged.getTotalElements()), consumablePaged);
    }

    @Override
    public ResponseEntity<?> updateConsumable(ConsumableDto consumableDto) {
        ConsumableDto consumableToUpdate = ConsumableMapper.MAPPER.toDto(getOrElseThrow(consumableDto.getId()));
        consumableRepository.findAllByBarcode(consumableDto.getBarcode()).stream()
                .filter(repeated -> !repeated.getId().equals(consumableDto.getId()))
                .findFirst()
                .ifPresent(repeated -> {
                    throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumableDto.getBarcode()));
                });
        ConsumableMapper.MAPPER.update(consumableDto, consumableToUpdate);

        consumableRepository.saveAndFlush(ConsumableMapper.MAPPER.toMo(consumableToUpdate));
        return Constructor.buildResponseObject(HttpStatus.OK, consumableDto);
    }

    @Override
    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        Consumable consumable = getOrElseThrow(consumableId);

        consumableRepository.deleteById(consumable.getId());
        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.CONSUMABLE_DELETED);
    }

    private Consumable getOrElseThrow(Integer consumableId) {
        return consumableRepository.findById(consumableId)
                .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(getErrorMessage(CONSUMABLE_NOT_FOUND), consumableId)));
    }

    @Override
    public ResponseEntity<?> loadExcel(MultipartFile file) {

        List<ConsumableExcelDto> consumableExcel = ConsumableExcelProcess.excelProcess(file);

        List<ConsumableDto> consumableToSave = convertExcelToConsumable(consumableExcel);

        consumableRepository.saveAll(consumableToSave.stream().map(ConsumableMapper.MAPPER::toMo).toList());

        return Constructor.buildResponseMessageObject(
                HttpStatus.OK,
                String.format(Messages.Info.TOOL_UPLOADED, consumableToSave.size()),
                consumableToSave);
    }

    private List<ConsumableDto> convertExcelToConsumable(List<ConsumableExcelDto> consumablesExcel) {
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
