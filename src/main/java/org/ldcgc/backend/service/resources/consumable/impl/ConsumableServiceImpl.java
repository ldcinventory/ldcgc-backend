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
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
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
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumableServiceImpl implements ConsumableService {

    private final ConsumableRepository consumableRepository;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final GroupRepository groupRepository;
    private final ConsumableExcelService consumableExcelService;

    public ResponseEntity<?> getConsumable(Integer consumableId) {
        return Constructor.buildResponseObject(HttpStatus.OK,
            ConsumableMapper.MAPPER.toDto(getOrElseThrowNotFound(consumableId)));
    }

    public ResponseEntity<?> createConsumable(ConsumableDto consumableDto) {
        if (Objects.nonNull(consumableDto.getId()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_ID_SHOULDNT_BE_PRESENT);

        if (consumableRepository.existsByBarcode(consumableDto.getBarcode()))
            throw new RequestException(HttpStatus.BAD_REQUEST,
                String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumableDto.getBarcode()));

        Consumable consumableEntity = ConsumableMapper.MAPPER.toMo(consumableDto);
        setLinkedEntitiesForConsumable(consumableEntity, consumableDto);

        consumableEntity = consumableRepository.saveAndFlush(consumableEntity);

        return Constructor.buildResponseMessageObject(HttpStatus.OK, Messages.Info.CONSUMABLE_CREATED, ConsumableMapper.MAPPER.toDto(consumableEntity));

    }

    public ResponseEntity<?> listConsumables(Integer pageIndex, Integer sizeIndex, String filterString, String sortField) {

        Pageable pageable = PageRequest.of(pageIndex, sizeIndex, Sort.by(sortField));

        Page<ConsumableDto> consumablePaged = consumableRepository.findByNameContainingOrDescriptionContaining(filterString, filterString, pageable)
            .map(ConsumableMapper.MAPPER::toDto);

        return Constructor.buildResponseMessageObject(
            HttpStatus.OK,
            String.format(Messages.Info.CONSUMABLE_LISTED, consumablePaged.getTotalElements()),
            consumablePaged);
    }

    public ResponseEntity<?> updateConsumable(ConsumableDto consumableDto, Integer consumableId) {
        List<Consumable> consumables = consumableRepository.findAllByBarcode(consumableDto.getBarcode());

        // if the barcode is used right now with multiple consumables
        if (consumables.size() > 1)
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                String.format(Messages.Error.CONSUMABLE_BARCODE_USED_MANY_TIMES, consumableDto.getBarcode()));
        // if the barcode is used by another consumable, other than informed to update
        else if (!consumables.isEmpty() && !Objects.equals(consumables.getFirst().getId(), consumableId))
            throw new RequestException(HttpStatus.BAD_REQUEST,
                String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumableDto.getBarcode()));

        Consumable consumableEntity = getOrElseThrowNotFound(consumableId);
        ConsumableMapper.MAPPER.update(consumableDto, consumableEntity);
        setLinkedEntitiesForConsumable(consumableEntity, consumableDto);
        consumableEntity = consumableRepository.saveAndFlush(consumableEntity);

        return Constructor.buildResponseObject(HttpStatus.CREATED, ConsumableMapper.MAPPER.toDto(consumableEntity));
    }

    public ResponseEntity<?> deleteConsumable(Integer consumableId) {
        consumableRepository.delete(getOrElseThrowNotFound(consumableId));

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.CONSUMABLE_DELETED);
    }

    public ResponseEntity<?> loadExcel(Integer groupId, MultipartFile file) {
        List<Consumable> consumablesToSave = consumableExcelService.excelToConsumables(file);

        consumablesToSave = consumableRepository.saveAll(consumablesToSave);

        return Constructor.buildResponseMessageObject(
            HttpStatus.CREATED,
            String.format(Messages.Info.CONSUMABLES_UPLOADED, consumablesToSave.size()),
            consumablesToSave.stream().map(ConsumableMapper.MAPPER::toDto).toList());
    }

    private Consumable getOrElseThrowNotFound(Integer consumableId) {
        return consumableRepository.findById(consumableId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CONSUMABLE_NOT_FOUND, consumableId)));
    }

    private void setLinkedEntitiesForConsumable(Consumable consumableEntity, ConsumableDto consumableDto) {
        Category brand = categoryRepository.findById(consumableDto.getBrand().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.BRAND_NOT_FOUND, consumableDto.getBrand())));

        Category consumableCategory = categoryRepository.findById(consumableDto.getCategory().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.CATEGORY_NOT_FOUND, consumableDto.getCategory().getId())));

        Location location = locationRepository.findById(consumableDto.getLocation().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.LOCATION_NOT_FOUND, consumableDto.getLocation().getId())));

        Group group = groupRepository.findById(consumableDto.getGroup().getId()).orElseThrow(() ->
            new RequestException(HttpStatus.BAD_REQUEST, String.format(Messages.Error.GROUP_NOT_FOUND, consumableDto.getGroup().getId())));

        consumableEntity.setBrand(brand);
        consumableEntity.setCategory(consumableCategory);
        consumableEntity.setLocation(location);
        consumableEntity.setGroup(group);

    }

}
