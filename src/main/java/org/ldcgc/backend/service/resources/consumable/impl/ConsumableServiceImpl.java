package org.ldcgc.backend.service.resources.consumable.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.category.Category;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.repository.category.CategoryRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.excel.ConsumableExcelDto;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumableServiceImpl implements ConsumableService {

    private final ConsumableRepository consumableRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;

    @Override
    public ResponseEntity<?> getConsumable(Integer consumableId) {
        Consumable consumable = getOrElseThrow(consumableId);
        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableMapper.MAPPER.toDto(consumable));
    }

    @Override
    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        if (Objects.nonNull(consumable.getId()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_ID_SHOULDNT_BE_PRESENT);

        if (consumableRepository.existsByBarcode(consumable.getBarcode()))
            throw new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumable.getBarcode()));

        Consumable consumableEntity = consumableRepository.saveAndFlush(ConsumableMapper.MAPPER.toMo(consumable));

        return Constructor.buildResponseObject(HttpStatus.CREATED, ConsumableMapper.MAPPER.toDto(consumableEntity));

    }

    @Override
    public ResponseEntity<?> listConsumables(Integer pageIndex, Integer sizeIndex, String sortField, String filterString) {
        Pageable pageable = PageRequest.of(pageIndex, sizeIndex, Sort.by(sortField));

        Page<ConsumableDto> consumablePaged = consumableRepository.findByNameContainingOrDescriptionContaining(filterString, filterString, pageable)
            .map(ConsumableMapper.MAPPER::toDto);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.CONSUMABLE_LISTED.formatted(consumablePaged.getTotalElements()), consumablePaged);

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

        Consumable consumableEntity = consumableRepository.saveAndFlush(ConsumableMapper.MAPPER.toMo(consumableToUpdate));

        return Constructor.buildResponseObject(HttpStatus.CREATED, ConsumableMapper.MAPPER.toDto(consumableEntity));

    }

    @Override
    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        Consumable consumable = getOrElseThrow(consumableId);

        consumableRepository.deleteById(consumable.getId());

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.CONSUMABLE_DELETED);

    }

    private Consumable getOrElseThrow(Integer consumableId) {
        return consumableRepository.findById(consumableId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_NOT_FOUND.formatted(consumableId)));
    }

    @Override
    public ResponseEntity<?> loadExcel(MultipartFile file) {

        List<ConsumableExcelDto> consumableExcel = ConsumableExcelProcess.excelProcess(file);

        List<Consumable> consumablesToSave = convertExcelToConsumable(consumableExcel);

        consumablesToSave = consumableRepository.saveAll(consumablesToSave);

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            String.format(Messages.Info.CONSUMABLES_UPLOADED, consumablesToSave.size()),
            consumablesToSave.stream().map(ConsumableMapper.MAPPER::toDto).toList());
    }

    private List<Consumable> convertExcelToConsumable(List<ConsumableExcelDto> consumablesExcel) {
        // extract barcodes and get all from DB
        List<String> consumablesIds= consumablesExcel.stream().map(ConsumableExcelDto::getBarcode).toList();
        List<ConsumableDto> consumable = consumableRepository
            .findByBarcodeIn(consumablesIds)
            .stream()
            .map(ConsumableMapper.MAPPER::toDto)
            .toList();

        // get categories from DB to "cache" in map
        Map<String, Category> categories = categoryRepository
            .findAllByParent_Name(CategoryParentEnum.CATEGORIES.getBbddName())
            .stream()
            .collect(Collectors.toMap(Category::getName, Function.identity()));
        Map<String, Category> brands = categoryRepository
            .findAllByParent_Name(CategoryParentEnum.BRANDS.getBbddName())
            .stream()
            .collect(Collectors.toMap(Category::getName, Function.identity()));
        Map<String, Category> stockTypes = categoryRepository
            .findAllByParent_Name(CategoryParentEnum.STOCKTYPE.getBbddName())
            .stream()
            .collect(Collectors.toMap(Category::getName, Function.identity()));
        Map<String, Location> locations = locationRepository
            .findAll()
            .stream()
            .collect(Collectors.toMap(Location::getName, Function.identity()));
        Map<String, Group> groups = groupRepository
            .findAll()
            .stream()
            .collect(Collectors.toMap(Group::getName, Function.identity()));

        // return consumables list (built entities)
        return consumablesExcel.stream()
            .map(consumableExcel -> Consumable.builder()
                .id(getIdByBarcode(consumableExcel, consumable))
                .barcode(consumableExcel.getBarcode())
                .category(categories.get(consumableExcel.getCategory()))
                .brand(brands.get(consumableExcel.getBrand()))
                .name(consumableExcel.getName())
                .model(consumableExcel.getModel())
                .description(consumableExcel.getDescription())
                .urlImages(consumableExcel.getUrlImages())
                .stock(consumableExcel.getStock())
                .stockType(stockTypes.get(consumableExcel.getStockType()))
                .location(locations.get(consumableExcel.getLocation()))
                .group(groups.get(consumableExcel.getGroup()))
                .build()
            ).toList();
    }

    private Integer getIdByBarcode(ConsumableExcelDto consumableExcel, List<ConsumableDto> consumable) {
        return consumable.stream()
            .filter(tool -> tool.getBarcode().equals(consumableExcel.getBarcode()))
            .map(ConsumableDto::getId)
            .findFirst()
            .orElse(null);
    }
}
