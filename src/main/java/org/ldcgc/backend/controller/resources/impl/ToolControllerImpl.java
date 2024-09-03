package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.resources.ToolController;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.ldcgc.backend.shared.enums.EOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ToolControllerImpl implements ToolController {

    private final ToolService toolService;

    public ResponseEntity<?> getTool(Integer toolId) {
        return toolService.getTool(toolId);
    }

    public ResponseEntity<?> getAllTools(String resourceType, String brand, String name, String model, String description, String barcode, String location, String status, Integer pageIndex, Integer size, String sortField, EOrder order) {
        return toolService.getAllTools(resourceType, brand, name, model, description, barcode, location, status, pageIndex, size, sortField, order);
    }

    public ResponseEntity<?> getAllToolsLoose(String filterString, String status, Integer pageIndex, Integer size, String sortField, EOrder order) {
        return toolService.getAllToolsLoose(filterString, status, pageIndex, size, sortField, order);
    }

    public ResponseEntity<?> createTool(ToolDto toolDto) {
        return toolService.createTool(toolDto);
    }

    public ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto) {
        return toolService.updateTool(toolId, toolDto);
    }

    public ResponseEntity<?> deleteTool(Integer toolId){
        return toolService.deleteTool(toolId);
    }

    public ResponseEntity<?> uploadToolsExcel(MultipartFile file, Integer initialRow) { return toolService.uploadToolsExcel(file, initialRow); }

    public ResponseEntity<?> uploadGSheetTemplate(String url, Integer initialRow) {
        return toolService.uploadGSheetTemplate(url, initialRow);
    }

}
