package org.ldcgc.backend.service.resources.tool;

import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ToolService {
    ResponseEntity<?> getTool(Integer toolId);
    ResponseEntity<?> createTool(ToolDto tool);
    ResponseEntity<?> updateTool(Integer toolId, ToolDto toolDto);
}
