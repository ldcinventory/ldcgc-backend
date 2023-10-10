package org.ldcgc.backend.service.resources.tool.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.model.resources.Status;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.resources.StatusRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ConsumableMapper;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.tool.ConsumableService;
import org.ldcgc.backend.util.creation.Constructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.ldcgc.backend.db.repository.resources.StatusRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.CONSUMABLE_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.ErrorMessage.STATUS_NOT_FOUND;
import static org.ldcgc.backend.util.retrieving.Message.getErrorMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsumableServiceImpl implements ConsumableService {

    private final ConsumableRepository consumableRepository;




    public ResponseEntity<?> getConsumable(Integer consumableId) {
        Consumable consumable = consumableRepository.findById(consumableId).orElseThrow(() ->
                new RequestException(HttpStatus.NOT_FOUND, String.format(getErrorMessage(CONSUMABLE_NOT_FOUND), consumableId)));

        return Constructor.buildResponseObject(HttpStatus.OK, ConsumableMapper.MAPPER.toDto(consumable));
    }

    public ResponseEntity<?> createConsumable(ConsumableDto consumable) {
        Consumable entityconsumable = ConsumableMapper.MAPPER.toMo(consumable);
        entityconsumable = consumableRepository.save(entityconsumable);
        return  Constructor.buildResponseObject(HttpStatus.OK, entityconsumable);
    }
}
