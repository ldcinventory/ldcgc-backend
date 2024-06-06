package org.ldcgc.backend.service.resources.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.category.BrandMapper;
import org.ldcgc.backend.payload.mapper.category.ResourceTypeMapper;
import org.ldcgc.backend.payload.mapper.group.GroupMapper;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.service.resources.tool.impl.ToolExcelServiceImpl;
import org.ldcgc.backend.strategy.MultipartFileFactory;
import org.ldcgc.backend.util.common.EXlsxToolPos;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.FORMULA;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomBrand;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomResourceType;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomToolDto;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getExcelAlphabetColumn;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;

@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
class ToolExcelServiceImplTest {

    @InjectMocks private ToolExcelServiceImpl toolExcelService;

    @Mock private ToolRepository toolRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ResourceTypeRepository resourceTypeRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private GroupService groupService;

    private List<ToolDto> toolsDto;
    private List<LocationDto> locationsDto;
    private List<GroupDto> groupsDto;

    @BeforeEach
    void init() {
        toolsDto = IntStream.range(0, 10).boxed().map(x ->
            getRandomToolDto()).toList();

        List<Tool> toolsDB = toolsDto.stream().map(ToolMapper.MAPPER::toMo).toList();
        List<Brand> brandsDB = toolsDB.stream().map(Tool::getBrand).toList();
        List<ResourceType> resourceTypesDB = toolsDB.stream().map(Tool::getResourceType).toList();

        groupsDto = toolsDB.stream().map(Tool::getGroup).map(GroupMapper.MAPPER::toDto).toList();
        locationsDto = toolsDB.stream().map(Tool::getLocation).map(LocationMapper.MAPPER::toDto).toList();

        lenient().doReturn(toolsDB).when(toolRepository).findAll();
        lenient().doReturn(brandsDB).when(brandRepository).findAll();
        lenient().doReturn(resourceTypesDB).when(resourceTypeRepository).findAll();
        // TODO
        lenient().doReturn(locationsDto).when(locationRepository).findAll();
        lenient().doReturn(groupsDto).when(groupService).getAllGroups();

    }

    @Test
    void excelToToolsShouldReturnTools() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, null);

        Map<String, Tool> toolsExcelResponse = toolExcelService.excelToTools(file, 0);

        assertEquals(toolsDto.size(), toolsExcelResponse.size());
    }

    @Test
    void excelToToolsShouldReturnWhatCellIsWrong() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, EXlsxToolPos.BARCODE);

        RequestException requestException = assertThrows(RequestException.class, () -> toolExcelService.excelToTools(file, 0));

        String expectedMessage = Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(1,
            EXlsxToolPos.BARCODE.getColumnNumber(),
            getExcelAlphabetColumn(EXlsxToolPos.BARCODE.getColumnNumber()),
            String.join(", ", new String[]{STRING.name(), FORMULA.name(), BLANK.name()}));

        assertEquals(expectedMessage, requestException.getMessage());
    }

    @Test
    void excelToToolsShouldThrowLocationNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, EXlsxToolPos.LOCATION);

        RequestException requestException = assertThrows(RequestException.class, () -> toolExcelService.excelToTools(file, 0));

        String expectedMessage = Messages.Error.EXCEL_VALUE_INCORRECT.formatted("mocked location", 1, EXlsxToolPos.LOCATION.getColumnNumber())
            .concat("\n")
            .concat(Messages.Error.LOCATION_NOT_FOUND_EXCEL.formatted("mocked location", locationsDto.stream()
                .sorted(Comparator.comparing(LocationDto::getName))
                .map(LocationDto::getName)
                .toList()
                .toString()));

        assertEquals(expectedMessage, requestException.getMessage());
    }

    @Test
    void excelToToolsShouldThrowMaintenancePeriodNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, EXlsxToolPos.MAINTENANCE_FREQUENCY);

        RequestException requestException = assertThrows(RequestException.class, () -> toolExcelService.excelToTools(file, 0));

        String expectedMessage = Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(1,
            EXlsxToolPos.MAINTENANCE_FREQUENCY.getColumnNumber(),
            getExcelAlphabetColumn(EXlsxToolPos.MAINTENANCE_FREQUENCY.getColumnNumber()),
            String.join(", ", new String[]{NUMERIC.name(), STRING.name(), FORMULA.name()}));

        assertEquals(expectedMessage, requestException.getMessage());
    }

    @Test
    void excelToToolsShouldThrowExcelParseException() throws IOException {
        MockMultipartFile file = spy(MultipartFileFactory.getXLSXFromTools(toolsDto, EXlsxToolPos.MAINTENANCE_FREQUENCY));

        doThrow(new IOException()).when(file).getInputStream();

        RequestException requestException = assertThrows(RequestException.class, () -> toolExcelService.excelToTools(file, 0));

        assertEquals(Messages.Error.EXCEL_PARSE_ERROR, requestException.getMessage());
    }

    @Test
    void excelToToolsShouldThrowCreateNewBrand() throws IOException {
        List<ToolDto> toolsDto = this.toolsDto.stream()
            .map(t -> t.toBuilder().brand(t.getBrand().toBuilder().name(t.getBrand().getName() + "-").build()).build())
            .toList();

        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, null);

        doReturn(BrandMapper.MAPPER.toEntity(getRandomBrand())).when(brandRepository).saveAndFlush(any(Brand.class));

        Map<String, Tool> toolsExcelResponse = toolExcelService.excelToTools(file, 0);

        assertEquals(toolsDto.size(), toolsExcelResponse.size());
    }

    @Test
    void excelToToolsShouldThrowCreateNewResourceType() throws IOException {
        List<ToolDto> toolsDto = this.toolsDto.stream()
            .map(t -> t.toBuilder().resourceType(t.getResourceType().toBuilder().name(t.getBrand().getName() + "-").build()).build())
            .toList();

        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, null);

        doReturn(ResourceTypeMapper.MAPPER.toEntity(getRandomResourceType())).when(resourceTypeRepository).saveAndFlush(any(ResourceType.class));

        Map<String, Tool> toolsExcelResponse = toolExcelService.excelToTools(file, 0);

        assertEquals(toolsDto.size(), toolsExcelResponse.size());
    }

    @Test
    void whenExcelToToolsWithBlankName_thenReturnNullTool() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithBlankBarcode_thenReturnToolWithRandomBarcode() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithBlankResourceType_thenReturnToolWithUnknownResourceType() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithBlankBrand_thenReturnToolWithUnknownBrand() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithBlankWeightUnits_thenReturnToolWithDefaultUnits() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithBlankWeightType_thenReturnToolWithUnknownLocation() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithBlankBrand_thenReturnToolWithDefaultBrand() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithBlankMaintenanceFrequency_thenReturnToolWithPeriod0() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithBlankMaintenanceFrequency_thenReturnToolWithMaintenanceTimeNever() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithLastMaintenanceNullAndMaintenanceFrequency_thenReturnToolWithMaintenanceTimeNever() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithLastMaintenanceIsNow_thenReturnToolWithCalculatedMaintenanceTime() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToToolsWithoutLocation_thenReturnToolWithoutLocation() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenExcelToTools_thenReturnGroupNotFoundInToken() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
