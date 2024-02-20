package org.ldcgc.backend.service.history.impl;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.ldcgc.backend.db.model.history.ConsumableRegister;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.model.users.Volunteer;
import org.ldcgc.backend.db.repository.history.ConsumableRegisterRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.history.ConsumableRegisterDto;
import org.ldcgc.backend.payload.dto.other.PaginationDetails;
import org.ldcgc.backend.payload.mapper.history.ConsumableRegisterMapper;
import org.ldcgc.backend.service.history.ConsumableRegisterService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
            -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND, registerId)));

        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableRegisterMapper.MAPPER.toDto(consumableRegister));
    }

    public ResponseEntity<?> listConsumableRegister(Integer pageIndex, Integer size, String builderAssistantId, String consumableBarcode, LocalDateTime dateFrom, LocalDateTime dateTo, String sortField) {
        Pageable paging = PageRequest.of(pageIndex, size, Sort.by(sortField).ascending());
        Page<ConsumableRegister> consumableRegisters;

        if (ObjectUtils.allNull(builderAssistantId, consumableBarcode, dateFrom, dateTo))
            consumableRegisters = consumableRegisterRepository.findAll(paging);
        else {
            List<ConsumableRegister> consumableRegisterList = consumableRegisterRepository.findAll((Specification<ConsumableRegister>) (consumableRegister, query, cb) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (dateFrom != null)
                    predicates.add(cb.greaterThanOrEqualTo(consumableRegister.get("registrationIn"), dateFrom));

                if (dateTo != null)
                    predicates.add(cb.lessThanOrEqualTo(consumableRegister.get("registrationOut"), dateTo));

                if (StringUtils.isNotEmpty(builderAssistantId)) {
                    Join<Volunteer, ConsumableRegister> volunteerConsumableRegisterJoin = consumableRegister.join("volunteer", JoinType.LEFT);
                    predicates.add(cb.and(cb.like(volunteerConsumableRegisterJoin.get("builderAssistantId"), "%" + builderAssistantId + "%")));
                }

                if (StringUtils.isNotEmpty(consumableBarcode)) {
                    Join<Consumable, ConsumableRegister> consumableConsumableRegisterJoin = consumableRegister.join("consumable", JoinType.LEFT);
                    predicates.add(cb.and(cb.like(consumableConsumableRegisterJoin.get("barcode"), "%" + consumableBarcode + "%")));
                }

                /*
                if (builderAssistantIds != null && builderAssistantIds.length > 0) {
                    Join<Volunteer, Absence> volunteerAbsenceJoin = absence.join("volunteer", JoinType.LEFT);
                    Expression<String> builderAssistantIdExpression = volunteerAbsenceJoin.get("builderAssistantId");
                    predicates.add(builderAssistantIdExpression.in(builderAssistantIds));
                }
                */

                return cb.and(predicates.toArray(new Predicate[0]));
            });

            int start = (int) paging.getOffset();
            int end = Math.min((start + paging.getPageSize()), consumableRegisterList.size());

            if (start > end)
                throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.PAGE_INDEX_REQUESTED_EXCEEDED_TOTAL);

            List<ConsumableRegister> pageContent = consumableRegisterList.subList(start, end);

            consumableRegisters = new PageImpl<>(pageContent, paging, consumableRegisterList.size());
        }

        if (pageIndex > consumableRegisters.getTotalPages())
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.PAGE_INDEX_REQUESTED_EXCEEDED_TOTAL);

        return Constructor.buildResponseMessageObjectPaged(HttpStatus.OK,
            String.format(Messages.Info.CONSUMABLE_REGISTER_LISTED, consumableRegisters.getTotalElements()),
            PaginationDetails.fromPaging(paging, consumableRegisters),
            consumableRegisters.getContent().stream().map(ConsumableRegisterMapper.MAPPER::toDto));
    }

    public ResponseEntity<?> createConsumableRegister(ConsumableRegisterDto consumableRegisterDto) {
        List<ConsumableRegister> consumableRegisters = consumableRegisterRepository
            .findAllByConsumable_Barcode(consumableRegisterDto.getConsumableBardcode());

        Consumable consumable = consumableRepository.findByBarcode(consumableRegisterDto.getConsumableBardcode())
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CONSUMABLE_NOT_FOUND, consumableRegisterDto.getId())));

        // if the consumable register exists and is completetly new, i.e.:
        // dto doesn't contain registrationOut field
        if(!CollectionUtils.isEmpty(consumableRegisters) && consumableRegisterDto.getRegistrationOut() == null) {
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

        // do not check the present amount if the parameter registrationOut in dto is present
        if (consumableRegisterDto.getStockAmountIn() > consumable.getStock() &&
            consumableRegisterDto.getRegistrationOut() == null)
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_NOT_ENOUGH_AMOUNT_ASSIGN);

        if (consumableRegisterDto.getRegistrationOut() != null &&
            consumableRegisterDto.getRegistrationOut().isAfter(LocalDateTime.now()))
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.Error.CONSUMABLE_REGISTER_RETURN_DATE_AFTER_TODAY);

        Volunteer volunteer = volunteerRepository.findByBuilderAssistantId(consumableRegisterDto.getVolunteerBAId())
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, Messages.Error.VOLUNTEER_NOT_FOUND));

        ConsumableRegister newConsumableRegister = ConsumableRegisterMapper.MAPPER.toEntity(consumableRegisterDto);
        if(Objects.isNull(newConsumableRegister.getStockAmountOut()))
            consumable.setStock(consumable.getStock() - newConsumableRegister.getStockAmountIn());
        if(ObjectUtils.allNotNull(newConsumableRegister.getStockAmountIn(), newConsumableRegister.getStockAmountOut()))
            consumable.setStock(consumable.getStock() + newConsumableRegister.getStockAmountOut());

        newConsumableRegister.setConsumable(consumable);
        newConsumableRegister.setVolunteer(volunteer);

        newConsumableRegister = consumableRegisterRepository.saveAndFlush(newConsumableRegister);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED,
            Messages.Info.CONSUMABLE_REGISTER_CREATED,
            ConsumableRegisterMapper.MAPPER.toDto(newConsumableRegister));
    }

    public ResponseEntity<?> updateConsumableRegister(Integer registerId, ConsumableRegisterDto consumableRegisterDto) {
        ConsumableRegister updateConsumableRegister = consumableRegisterRepository.findById(registerId)
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND, registerId)));

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
            .orElseThrow(() -> new RequestException(HttpStatus.NOT_FOUND, String.format(Messages.Error.CONSUMABLE_REGISTER_NOT_FOUND, registerId)));

        consumableRegisterRepository.delete(consumableRegister);

        return Constructor.buildResponseMessage(HttpStatus.OK, Messages.Info.CONSUMABLE_REGISTER_DELETED);
    }
}
