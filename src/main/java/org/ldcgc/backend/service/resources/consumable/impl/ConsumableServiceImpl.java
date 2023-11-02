package org.ldcgc.backend.service.resources.consumable.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.service.resources.consumable.ConsumableService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.CONSUMABLE_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.InfoMessage.CONSUMABLE_LISTED;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;
import static org.ldcgc.backend.util.retrieving.Message.getInfoMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumableServiceImpl implements ConsumableService {

    @Autowired
    private final ConsumableRepository consumableRepository;

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
        //System.out.println(pageIndex + " "+sizeIndex + " " + filterString);

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
}
