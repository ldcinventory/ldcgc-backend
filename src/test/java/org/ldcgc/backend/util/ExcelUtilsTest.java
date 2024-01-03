package org.ldcgc.backend.util;

import org.junit.jupiter.api.Test;
import org.ldcgc.backend.payload.dto.excel.ToolExcelDto;
import org.ldcgc.backend.strategy.MultipartFileFactory;
import org.ldcgc.backend.service.excel.impl.ToolExcelServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ExcelUtilsTest {

    private final PodamFactory factory = new PodamFactoryImpl();
    @Test
    void excelToToolsShouldTransform() throws IOException {
        List<ToolExcelDto> toolsExcel = factory.manufacturePojo(ArrayList.class, ToolExcelDto.class);
        MultipartFile file = MultipartFileFactory.getFileFromTools(toolsExcel);

        List<ToolExcelDto> toolsExcelResponse = ToolExcelServiceImpl.excelToTools(file);

        assertEquals(toolsExcel.size(), toolsExcelResponse.size());
    }

    @Test
    void excelToToolsShouldIndicateWhatCellIsWrong(){
        ToolExcelDto.builder().
        MultipartFile file = MultipartFileFactory.getFileFromTools(toolsExcel);
        assertThrows(ToolExcelServiceImpl.excelToTools())
    }
}
