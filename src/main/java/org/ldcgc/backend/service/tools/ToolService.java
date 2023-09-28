package org.ldcgc.backend.service.tools;

import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ToolService {
    ResponseEntity<?> createTool(ToolDto tool);
}
