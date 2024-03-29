package org.ldcgc.backend.service.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.common.EStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ToolService {
    ResponseEntity<?> getTool(Integer toolId);
    ResponseEntity<?> createTool(ToolDto toolDto);
    ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto);
    ResponseEntity<?> deleteTool(Integer toolId);
    ResponseEntity<?> getAllTools(Integer pageIndex, Integer size, String category, String brand, String name, String model, String description, String status, String sortField);
    ResponseEntity<?> uploadToolsExcel(MultipartFile file);
    Tool updateToolStatus(Tool tool, EStatus status);
}
