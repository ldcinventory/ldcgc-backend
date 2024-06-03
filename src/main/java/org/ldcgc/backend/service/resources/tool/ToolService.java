package org.ldcgc.backend.service.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.util.common.EOrder;
import org.ldcgc.backend.util.common.EToolStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ToolService {

    ResponseEntity<?> getTool(Integer toolId);
    ResponseEntity<?> getAllTools(String resourceType, String brand, String name, String model, String description, String barcode, String location, String status, Integer pageIndex, Integer size, String sortField, EOrder order);
    ResponseEntity<?> getAllToolsLoose(String filterString, String status, Integer pageIndex, Integer size, String sortField, EOrder order);
    ResponseEntity<?> createTool(ToolDto toolDto);
    ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto);
    ResponseEntity<?> deleteTool(Integer toolId);
    ResponseEntity<?> uploadToolsExcel(MultipartFile file);
    ResponseEntity<?> loadGSheetTemplate(String url);

    Tool updateToolStatus(Tool tool, EToolStatus status);

}
