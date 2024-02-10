package org.ldcgc.backend.service.history.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.history.ConsumableRegisterRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.payload.mapper.history.ConsumableRegisterMapper;
import org.ldcgc.backend.service.history.ConsumableRegisterService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ConsumableRegisterServiceImpl implements ConsumableRegisterService {

    private final ConsumableRegisterRepository consumableRegisterRepository;
    private final ConsumableRepository consumableRepository;
    private final VolunteerRepository volunteerRepository;

    public ResponseEntity<?> getConsumableRegister(Integer registerId) {
        ConsumableRegister consumableRegister = consumableRegisterRepository.findById(registerId).orElseThrow(()
            -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND));

        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableRegisterMapper.MAPPER.toDto(consumableRegister));
    }

    public ResponseEntity<?> listConsumableRegister(Integer pageIndex, Integer size, String builderAssistantId, String consumableBarcode, LocalDate dateFrom, LocalDate dateTo, String sortField) {
        List<ConsumableRegister> consumableRegisters = new ArrayList<>();

        return Constructor.buildResponseMessageObject(HttpStatus.OK,
            String.format(Messages.Info.CONSUMABLE_REGISTER_LISTED, consumableRegisters.size()),
            consumableRegisters.stream().map(ConsumableRegisterMapper.MAPPER::toDto));
    }

    public ResponseEntity<?> createConsumableRegister(ConsumableRegisterDto consumableRegisterDto) {
        List<ConsumableRegister> consumableRegisters = consumableRegisterRepository
            .findAllByConsumable_Barcode(consumableRegisterDto.getConsumableBardcode());

        Consumable consumable = consumableRepository.findByBarcode(consumableRegisterDto.getConsumableBardcode())
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_NOT_FOUND));

        if(!CollectionUtils.isEmpty(consumableRegisters)) {
            if (consumableRegisters.stream().anyMatch(cr -> cr.getVolunteer().getBuilderAssistantId().equals(consumableRegisterDto.getVolunteerBAId())))
                throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_VOLUNTEER_DUPLICATED);

            if (consumableRegisters.stream().map(ConsumableRegister::getStockAmountIn)
                    .reduce(0.0f, Float::sum) + consumableRegisterDto.getStockAmountIn() > consumable.getStock())
                throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_NOT_ENOUGH_AMOUNT_ASSIGN);
        }

        if (Objects.nonNull(consumableRegisterDto.getRegistrationOut()) &&
            consumableRegisterDto.getRegistrationOut().isBefore(consumableRegisterDto.getRegistrationIn()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_RETURN_DATE_BEFORE_ASSIGN);

        if (Objects.nonNull(consumableRegisterDto.getRegistrationOut()) &&
            consumableRegisterDto.getRegistrationOut().isAfter(LocalDateTime.now()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_RETURN_DATE_AFTER_TODAY);

        if (consumableRegisterDto.getStockAmountIn() > consumable.getStock())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_NOT_ENOUGH_AMOUNT_ASSIGN);

        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(consumableRegisterDto.getVolunteerBAId())
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        ConsumableRegister newConsumableRegister = ConsumableRegisterMapper.MAPPER.toEntity(consumableRegisterDto);
        newConsumableRegister.setConsumable(consumable);
        newConsumableRegister.setVolunteer(volunteer);

        newConsumableRegister = consumableRegisterRepository.saveAndFlush(newConsumableRegister);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED,
            Messages.Info.CONSUMABLE_REGISTER_CREATED,
            ConsumableRegisterMapper.MAPPER.toDto(newConsumableRegister));
    }

    public ResponseEntity<?> updateConsumableRegister(Integer registerId, ConsumableRegisterDto consumableRegisterDto) {
        ConsumableRegister updateConsumableRegister = consumableRegisterRepository.findById(registerId)
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND));

        if (Objects.nonNull(updateConsumableRegister.getRegistrationOut()) &&
            updateConsumableRegister.getRegistrationOut().isBefore(updateConsumableRegister.getRegistrationIn()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_RETURN_DATE_BEFORE_ASSIGN);

        if (Objects.nonNull(updateConsumableRegister.getRegistrationOut()) &&
            updateConsumableRegister.getRegistrationOut().isAfter(LocalDateTime.now()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_RETURN_DATE_AFTER_TODAY);

        if (updateConsumableRegister.getStockAmountIn() > updateConsumableRegister.getConsumable().getStock())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_NOT_ENOUGH_AMOUNT_ASSIGN);

        if (Objects.nonNull(updateConsumableRegister.getStockAmountOut()) &&
            updateConsumableRegister.getStockAmountOut() > updateConsumableRegister.getStockAmountIn())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_NOT_ENOUGH_AMOUNT_RETURN);


        Consumable consumable = consumableRepository.findByBarcode(consumableRegisterDto.getConsumableBardcode())
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_NOT_FOUND));

        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(consumableRegisterDto.getVolunteerBAId())
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        ConsumableRegisterMapper.MAPPER.update(consumableRegisterDto, updateConsumableRegister);
        updateConsumableRegister.setConsumable(consumable);
        updateConsumableRegister.setVolunteer(volunteer);

        updateConsumableRegister = consumableRegisterRepository.saveAndFlush(updateConsumableRegister);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED,
            Messages.Info.CONSUMABLE_REGISTER_UPDATED,
            ConsumableRegisterMapper.MAPPER.toDto(updateConsumableRegister));
    }

    public ResponseEntity<?> deleteConsumableRegister(Integer registerId) {
        ConsumableRegister consumableRegister = consumableRegisterRepository.findById(registerId)
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND));

        consumableRegisterRepository.delete(consumableRegister);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.CONSUMABLE_REGISTER_DELETED);
    }
}
