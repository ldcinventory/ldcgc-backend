package org.ldcgc.backend.service.resources.tool;

import org.ldcgc.backend.payload.dto.excel.ToolExcelDto;
import org.ldcgc.backend.payload.dto.excel.ToolExcelMasterDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ToolExcelService {
    List<ToolDto> convertExcelToTools(List<ToolExcelDto> toolsExcel);
}
