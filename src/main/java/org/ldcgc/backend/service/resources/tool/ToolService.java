package org.ldcgc.backend.service.resources.tool;

import org.ldcgc.backend.payload.dto.excel.ToolExcelDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ToolService {
    ResponseEntity<?> getTool(Integer toolId);
    ResponseEntity<?> createTool(ToolDto tool);
    ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto);
    ResponseEntity<?> deleteTool(Integer toolId);
    ResponseEntity<?> getAllTools(Integer pageIndex, Integer size, String filterString);
    ResponseEntity<?> uploadToolsExcel(MultipartFile file);
}
