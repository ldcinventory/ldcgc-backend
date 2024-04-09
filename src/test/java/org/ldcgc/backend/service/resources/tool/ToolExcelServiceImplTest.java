package org.ldcgc.backend.service.resources.tool;

import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.mock.MockedResources;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.group.GroupMapper;
import org.ldcgc.backend.payload.mapper.location.LocationMapper;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.impl.ToolExcelServiceImpl;
import org.ldcgc.backend.strategy.MultipartFileFactory;
import org.ldcgc.backend.util.common.EXlsxToolPos;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.util.conversion.ExcelFunctions.getExcelAlphabetColumn;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ToolExcelServiceImplTest {

    @InjectMocks private ToolExcelServiceImpl toolExcelService;

    @Mock private ToolRepository toolRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ResourceTypeRepository resourceTypeRepository;
    @Mock private LocationService locationService;
    @Mock private GroupService groupService;

    private List<ToolDto> toolsDto;
    private List<Tool> toolsDB;
    private List<Brand> brandsDB;
    private List<ResourceType> resourceTypesDB;
    private List<LocationDto> locationsDto;
    private List<GroupDto> groupsDto;

    @BeforeEach
    void init() {
        toolsDto = IntStream.range(0, 10).boxed().map(x ->
            MockedResources.getRandomToolDto()).toList();

        toolsDB = toolsDto.stream().map(ToolMapper.MAPPER::toMo).toList();
        brandsDB = toolsDB.stream().map(Tool::getBrand).toList();
        resourceTypesDB = toolsDB.stream().map(Tool::getResourceType).toList();

        groupsDto = toolsDB.stream().map(Tool::getGroup).map(GroupMapper.MAPPER::toDto).toList();
        locationsDto = toolsDB.stream().map(Tool::getLocation).map(LocationMapper.MAPPER::toDto).toList();

        doReturn(toolsDB).when(toolRepository).findAll();
        doReturn(brandsDB).when(brandRepository).findAll();
        doReturn(resourceTypesDB).when(resourceTypeRepository).findAll();
        doReturn(locationsDto).when(locationService).getAllLocations();
        doReturn(groupsDto).when(groupService).getAllGroups();

    }

    @Test
    void excelToToolsShouldReturnTools() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, null);

        List<ToolDto> toolsExcelResponse = toolExcelService.excelToTools(file);

        assertEquals(toolsDto.size(), toolsExcelResponse.size());
    }

    @Test
    void excelToToolsShouldReturnWhatCellIsWrong() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, EXlsxToolPos.BARCODE);

        RequestException requestException = assertThrows(RequestException.class, () -> toolExcelService.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_CELL_TYPE_ERROR.formatted(2, 1, getExcelAlphabetColumn(0)), requestException.getMessage());
    }

    @Test
    void excelToToolsShouldThrowLocationNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, EXlsxToolPos.LOCATION);

        RequestException requestException = assertThrows(RequestException.class, () -> toolExcelService.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_VALUE_INCORRECT.formatted("mocked location", 1, EXlsxToolPos.LOCATION.getColumnNumber()).concat("\n").concat(Messages.Error.LOCATION_NOT_FOUND_EXCEL
                .formatted("mocked location", locationsDto.stream().sorted(Comparator.comparing(LocationDto::getName)).map(LocationDto::getName).toList().toString())),
                requestException.getMessage());
    }

    @Test
    void excelToToolsShouldThrowGroupNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, EXlsxToolPos.GROUP);

        RequestException requestException = assertThrows(RequestException.class, () -> toolExcelService.excelToTools(file));

        assertEquals(requestException.getMessage(), Messages.Error.EXCEL_VALUE_INCORRECT.formatted("mocked group", 1, EXlsxToolPos.GROUP.getColumnNumber()).concat("\n").concat(Messages.Error.GROUP_NOT_FOUND_EXCEL
            .formatted("mocked group", groupsDto.stream().sorted(Comparator.comparing(GroupDto::getName)).map(GroupDto::getName).collect(Collectors.toCollection(TreeSet::new)).toString())));
    }

    @Test
    void excelToToolsShouldThrowMaintenancePeriodNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getXLSXFromTools(toolsDto, EXlsxToolPos.MAINTENANCE_PERIOD);

        RequestException requestException = assertThrows(RequestException.class, () -> toolExcelService.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(1, EXlsxToolPos.MAINTENANCE_PERIOD.getColumnNumber(), getExcelAlphabetColumn(EXlsxToolPos.MAINTENANCE_PERIOD.getColumnNumber()), CellType.NUMERIC.toString()),
                requestException.getMessage());
    }

    @Test
    void excelToToolsShouldThrowExcelParseException() {
        fail("Not yet implemented");
    }

    @Test
    void excelToToolsShouldThrowCreateNewBrand() {
        fail("Not yet implemented");
    }

    @Test
    void excelToToolsShouldThrowCreateNewResourceType() {
        fail("Not yet implemented");
    }

}
