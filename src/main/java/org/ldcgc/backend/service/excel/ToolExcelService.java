package org.ldcgc.backend.service.excel;

import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ToolExcelService {
    List<ToolDto> excelToTools(MultipartFile excel);
}
