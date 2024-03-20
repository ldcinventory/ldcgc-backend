package org.ldcgc.backend.controller.resources.impl;

import lombok.RequiredArgsConstructor;
import org.ldcgc.backend.controller.resources.ToolController;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ToolControllerImpl implements ToolController {

    private final ToolService toolService;

    public ResponseEntity<?> getTool(Integer toolId) { return toolService.getTool(toolId); }
    public ResponseEntity<?> createTool(ToolDto toolDto) {
        return toolService.createTool(toolDto);
    }


    public ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto) { return toolService.updateTool(toolId, toolDto); }

    public ResponseEntity<?> deleteTool(Integer toolId){
        return toolService.deleteTool(toolId);
    }

    public ResponseEntity<?> getAllTools(Integer pageIndex, Integer size, String category, String brand, String name, String model, String description, String status, String sortField) {
        return toolService.getAllTools(pageIndex, size, category, brand, name, model, description, status, sortField);
    }

    public ResponseEntity<?> uploadToolsExcel(MultipartFile file) { return toolService.uploadToolsExcel(file); }

    @Override
    public ResponseEntity<?> getAllToolsLoose(Integer pageIndex, Integer size, String filterString, String status, String sortField) {
        return toolService.getAllToolsLoose(pageIndex, size, filterString, status, sortField);
    }


}
