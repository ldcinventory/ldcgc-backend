package org.ldcgc.backend.service.resources.tool;

import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.base.mock.MockedResources;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.category.ResourceTypeDto;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.group.GroupService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.impl.ToolExcelServiceImpl;
import org.ldcgc.backend.strategy.MultipartFileFactory;
import org.ldcgc.backend.util.common.EExcelToolsPositions;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@SpringBootTest
class ToolExcelServiceImplTest {

    @InjectMocks private ToolExcelServiceImpl service;

    @Mock private ToolRepository toolRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ResourceTypeRepository resourceTypeRepository;
    @Mock private LocationService locationService;
    @Mock private GroupService groupService;

    private List<Tool> toolsDb;
    private List<ToolDto> tools;
    private List<BrandDto> brands;
    private List<ResourceTypeDto> resourceTypes;
    private List<LocationDto> locations;
    private List<GroupDto> groups;

    @BeforeEach
    void init() {
        toolsDb.addAll(IntStream.range(0, 10).boxed()
            .map(x -> ToolMapper.MAPPER.toMo(MockedResources.getRandomToolDto()))
            .toList());
        brands.addAll(IntStream.range(0, 10).boxed()
            .map(x -> MockedResources.getRandomBrand())
            .toList());
        resourceTypes.addAll(IntStream.range(0, 10).boxed()
            .map(x -> MockedResources.getRandomResourceType())
            .toList());
        locations.addAll(IntStream.range(0, 10).boxed()
            .map(x -> MockedResources.getRandomLocation())
            .toList());
        groups.addAll(IntStream.range(0, 10).boxed()
            .map(x -> MockedResources.getRandomGroup())
            .toList());
        tools = toolsDb.stream().map(ToolMapper.MAPPER::toDto).toList();
    }

    @Test
    void excelToToolsShouldTransform() throws IOException {
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, null);

        doReturn(toolsDb).when(toolRepository).findAll();
        doReturn(brands).when(brandRepository).findAll();
        doReturn(resourceTypes).when(resourceTypeRepository).findAll();
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupService).getAllGroups();

        List<ToolDto> toolsExcelResponse = service.excelToTools(file);

        assertEquals(tools.size(), toolsExcelResponse.size());
    }

    @Test
    void excelToToolsShouldIndicateWhatCellIsWrong() throws IOException {
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, EExcelToolsPositions.BARCODE);

        doReturn(toolsDb).when(toolRepository).findAll();
        doReturn(brands).when(brandRepository).findAll();
        doReturn(resourceTypes).when(resourceTypeRepository).findAll();
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(1, 0, CellType.STRING.toString()), requestException.getMessage());
    }
    @Test
    void excelToToolsShouldIndicateBrandNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, EExcelToolsPositions.BRAND);

        doReturn(toolsDb).when(toolRepository).findAll();
        doReturn(brands).when(brandRepository).findAll();
        doReturn(resourceTypes).when(resourceTypeRepository).findAll();
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_VALUE_INCORRECT.formatted("made up brand", 1, EExcelToolsPositions.BRAND.getColumnNumber()).concat("\n").concat(Messages.Error.CATEGORY_SON_NOT_FOUND
                .formatted(CategoryParentEnum.BRANDS.getName(), "made up brand", CategoryParentEnum.BRANDS.getName(), brands.stream().sorted(Comparator.comparing(BrandDto::getName)).map(BrandDto::getName).toList().toString())),
                requestException.getMessage());
    }
    @Test
    void excelToToolsShouldIndicateCategoryNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, EExcelToolsPositions.RESOURCE_TYPE);


        doReturn(toolsDb).when(toolRepository).findAll();
        doReturn(brands).when(brandRepository).findAll();
        doReturn(resourceTypes).when(resourceTypeRepository).findAll();
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_VALUE_INCORRECT.formatted("made up resource type", 1, EExcelToolsPositions.RESOURCE_TYPE.getColumnNumber()).concat("\n").concat(Messages.Error.CATEGORY_SON_NOT_FOUND
                .formatted(CategoryParentEnum.CATEGORIES.getName(), "made up resource type", CategoryParentEnum.CATEGORIES.getName(), resourceTypes.stream().sorted(Comparator.comparing(ResourceTypeDto::getName)).map(ResourceTypeDto::getName).toList().toString())),
                requestException.getMessage());
    }
    @Test
    void excelToToolsShouldIndicateLocationNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, EExcelToolsPositions.LOCATION);

        doReturn(toolsDb).when(toolRepository).findAll();
        doReturn(brands).when(brandRepository).findAll();
        doReturn(resourceTypes).when(resourceTypeRepository).findAll();
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_VALUE_INCORRECT.formatted("made up location", 1, EExcelToolsPositions.LOCATION.getColumnNumber()).concat("\n").concat(Messages.Error.LOCATION_NOT_FOUND_EXCEL
                .formatted("made up location", locations.stream().sorted(Comparator.comparing(LocationDto::getName)).map(LocationDto::getName).toList().toString())),
                requestException.getMessage());
    }
    @Test
    void excelToToolsShouldIndicateGroupNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, EExcelToolsPositions.GROUP);

        doReturn(toolsDb).when(toolRepository).findAll();
        doReturn(brands).when(brandRepository).findAll();
        doReturn(resourceTypes).when(resourceTypeRepository).findAll();
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_VALUE_INCORRECT.formatted("made up group", 1, EExcelToolsPositions.GROUP.getColumnNumber()).concat("\n").concat(Messages.Error.GROUP_NOT_FOUND_EXCEL
                .formatted("made up group", groups.stream().sorted(Comparator.comparing(GroupDto::getName)).map(GroupDto::getName).toList().toString())),
                requestException.getMessage());
    }
    @Test
    void excelToToolsShouldIndicateMaintenancePeriodNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, EExcelToolsPositions.MAINTENANCE_PERIOD);

        doReturn(toolsDb).when(toolRepository).findAll();
        doReturn(brands).when(brandRepository).findAll();
        doReturn(resourceTypes).when(resourceTypeRepository).findAll();
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(1, EExcelToolsPositions.MAINTENANCE_PERIOD.getColumnNumber(), CellType.NUMERIC.toString()),
                requestException.getMessage());
    }
    @Test
    void excelToToolsShouldIndicateLastMaintenanceNotFound() throws IOException {
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, EExcelToolsPositions.LAST_MAINTENANCE);

        doReturn(toolsDb).when(toolRepository).findAll();
        doReturn(brands).when(brandRepository).findAll();
        doReturn(resourceTypes).when(resourceTypeRepository).findAll();
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(1, EExcelToolsPositions.LAST_MAINTENANCE.getColumnNumber(), CellType.NUMERIC.toString()),
                requestException.getMessage());
    }

    @Test
    void whenIOException_shouldThrowRequestException() throws IOException {
        MultipartFile mockExcel = mock(MultipartFile.class);

        doThrow(new IOException()).when(mockExcel).getInputStream();

        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(mockExcel));

        assertEquals(Messages.Error.EXCEL_PARSE_ERROR, requestException.getMessage());
    }
}
