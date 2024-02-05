package org.ldcgc.backend.service.resources.consumable.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.resources.Consumable;
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
    private final ConsumableExcelService consumableExcelService;

    public ResponseEntity<?> getConsumable(Integer consumableId) {
        return Constructor.buildResponseObject(HttpStatus.OK,
            ConsumableMapper.MAPPER.toDto(getOrElseThrowNotFound(consumableId)));
    }

    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        if(Objects.nonNull(consumable.getId()))
            throw new RequestException(HttpStatus.BAD_REQUEST,
                String.format(Messages.Error.CONSUMABLE_ID_SHOULDNT_BE_PRESENT, consumable.getBarcode()));

        if (consumableRepository.existsByBarcode(consumable.getBarcode()))
            throw new RequestException(HttpStatus.BAD_REQUEST,
                String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumable.getBarcode()));

        Consumable consumableEntity = consumableRepository.saveAndFlush(ConsumableMapper.MAPPER.toMo(consumable));
        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableMapper.MAPPER.toDto(consumableEntity));
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
        if(consumableRepository.findAllByBarcode(consumableDto.getBarcode()).size() > 1)
            throw new RequestException(HttpStatus.UNPROCESSABLE_ENTITY,
                String.format(Messages.Error.CONSUMABLE_BARCODE_USED_MANY_TIMES, consumableDto.getBarcode()));
        // if the barcode is used by another consumable, other than informed to update
        else if(consumables.get(0).getId() != consumableId)
            throw new RequestException(HttpStatus.BAD_REQUEST,
                String.format(Messages.Error.CONSUMABLE_BARCODE_ALREADY_EXISTS, consumableDto.getBarcode()));

        Consumable consumableEntity = getOrElseThrowNotFound(consumableDto.getId());
        ConsumableMapper.MAPPER.update(consumableDto, consumableEntity);
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
            String.format(Messages.Info.TOOL_UPLOADED, consumablesToSave.size()),
            consumablesToSave.stream().map(ConsumableMapper.MAPPER::toDto).toList());
    }

    private Consumable getOrElseThrowNotFound(Integer consumableId) {
        return consumableRepository.findById(consumableId).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CONSUMABLE_NOT_FOUND, consumableId)));
    }

}
