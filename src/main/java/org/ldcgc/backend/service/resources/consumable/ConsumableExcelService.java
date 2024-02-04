package org.ldcgc.backend.service.resources.consumable;

import org.ldcgc.backend.payload.dto.resources.ConsumableDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ConsumableExcelService {
    List<ConsumableDto> excelToConsumable(MultipartFile excel);
}
