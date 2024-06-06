package org.ldcgc.backend.service.resources.consumable;

import org.ldcgc.backend.db.model.resources.Consumable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public interface ConsumableExcelService {
    Map<String, Consumable> excelToConsumables(MultipartFile excel, int initialRow);
}
