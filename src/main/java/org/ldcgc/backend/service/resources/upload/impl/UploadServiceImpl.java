package org.ldcgc.backend.service.resources.upload.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.db.model.resources.Consumable;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.mapper.resources.consumable.ConsumableMapper;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.upload.UploadService;
import org.ldcgc.backend.util.creation.Constructor;
import org.ldcgc.backend.util.retrieving.Messages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final ToolRepository toolRepository;
    private final ConsumableRepository consumableRepository;

    private final String toolsFolder = "/tools";
    private final String consumablesFolder = "/consumables";

    public ResponseEntity<?> uploadToolImages(String toolBarcode, MultipartFile[] images) {
        Tool tool = toolRepository.findFirstByBarcode(toolBarcode).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.TOOL_NOT_FOUND));

        String[] urlImages = uploadToGDrive(images, toolsFolder);
        tool.setUrlImages(urlImages);

        tool = toolRepository.saveAndFlush(tool);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.TOOL_UPDATED, ToolMapper.MAPPER.toDto(tool));
    }

    public ResponseEntity<?> uploadConsumableImages(String consumableBarcode, MultipartFile[] images) {
        Consumable consumable = consumableRepository.findByBarcode(consumableBarcode).orElseThrow(() ->
            new RequestException(HttpStatus.NOT_FOUND, Messages.Error.CONSUMABLE_NOT_FOUND));

        String[] urlImages = uploadToGDrive(images, consumablesFolder);
        consumable.setUrlImages(urlImages);

        consumable = consumableRepository.saveAndFlush(consumable);

        return Constructor.buildResponseMessageObject(HttpStatus.CREATED, Messages.Info.CONSUMABLE_UPDATED, ConsumableMapper.MAPPER.toDto(consumable));
    }

    private String[] uploadToGDrive(MultipartFile[] images, String folder) {
        return null;
    }
}
