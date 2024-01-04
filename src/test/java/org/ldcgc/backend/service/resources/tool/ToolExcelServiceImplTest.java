package org.ldcgc.backend.service.resources.tool;

import org.springframework.boot.test.context.SpringBootTest;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ToolExcelServiceImplTest {

    private final PodamFactory factory = new PodamFactoryImpl();
/*    @Test
    void excelToToolsShouldTransform() throws IOException {
        List<ToolExcelDto> toolsExcel = factory.manufacturePojo(ArrayList.class, ToolExcelDto.class);
        MultipartFile file = MultipartFileFactory.getFileFromTools(toolsExcel);

        List<ToolExcelDto> toolsExcelResponse = org.ldcgc.backend.service.resources.tool.impl.ToolExcelServiceImpl.excelToTools(file);

        assertEquals(toolsExcel.size(), toolsExcelResponse.size());
    }

    @Test
    void excelToToolsShouldIndicateWhatCellIsWrong(){
        ToolExcelDto.builder().
        MultipartFile file = MultipartFileFactory.getFileFromTools(toolsExcel);
        assertThrows(org.ldcgc.backend.service.resources.tool.impl.ToolExcelServiceImpl.excelToTools())
    }*/
}
