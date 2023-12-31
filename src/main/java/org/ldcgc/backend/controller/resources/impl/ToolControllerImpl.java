package org.ldcgc.backend.controller.resources.impl;

import org.ldcgc.backend.controller.resources.ToolController;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.service.resources.tool.ToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ToolControllerImpl implements ToolController {

    @Autowired
    private ToolService toolService;

    public ResponseEntity<?> getTool(Integer toolId) { return toolService.getTool(toolId); }

    public ResponseEntity<?> createTool(ToolDto tool) {
        return toolService.createTool(tool);
    }

    public ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto) { return toolService.updateTool(toolId, toolDto); }

    public ResponseEntity<?> deleteTool(Integer toolId){
        return toolService.deleteTool(toolId);
    }

    public ResponseEntity<?> getAllTools(Integer pageIndex, Integer size, String sortField, String brand, String model, String description, String status) {
        return toolService.getAllTools(pageIndex, size, sortField, brand, model, description, status);
    }

    public ResponseEntity<?> uploadToolsExcel(MultipartFile file) { return toolService.uploadToolsExcel(file); }


}
