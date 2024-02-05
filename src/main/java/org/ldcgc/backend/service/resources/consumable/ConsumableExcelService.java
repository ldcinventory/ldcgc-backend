package org.ldcgc.backend.service.resources.consumable;

import org.ldcgc.backend.db.model.resources.Consumable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ConsumableExcelService {
    List<Consumable> excelToConsumables(MultipartFile excel);

}
