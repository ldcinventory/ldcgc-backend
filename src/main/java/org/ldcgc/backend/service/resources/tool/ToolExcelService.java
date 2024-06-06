package org.ldcgc.backend.service.resources.tool;

import org.ldcgc.backend.db.model.resources.Tool;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public interface ToolExcelService {
    Map<String, Tool> excelToTools(MultipartFile excel, int initialRow);
}
