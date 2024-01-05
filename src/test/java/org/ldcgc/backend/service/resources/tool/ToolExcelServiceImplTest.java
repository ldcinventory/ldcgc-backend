package org.ldcgc.backend.service.resources.tool;

import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.CategoryDto;
import org.ldcgc.backend.payload.dto.category.CategoryParentEnum;
import org.ldcgc.backend.payload.dto.group.GroupDto;
import org.ldcgc.backend.payload.dto.location.LocationDto;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.category.CategoryService;
import org.ldcgc.backend.service.groups.GroupsService;
import org.ldcgc.backend.service.location.LocationService;
import org.ldcgc.backend.service.resources.tool.impl.ToolExcelServiceImpl;
import org.ldcgc.backend.strategy.MultipartFileFactory;
import org.ldcgc.backend.util.retrieving.Messages;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@SpringBootTest
class ToolExcelServiceImplTest {

    @InjectMocks
    private ToolExcelServiceImpl service;

    @Mock
    private ToolRepository toolRepository;
    @Mock
    private CategoryService categoryService;
    @Mock
    private LocationService locationService;
    @Mock
    private GroupsService groupsService;
    private final PodamFactory factory = new PodamFactoryImpl();

    @Test
    void excelToToolsShouldTransform() throws IOException {
        List<ToolDto> tools = factory.manufacturePojo(ArrayList.class, ToolDto.class);
        List<Tool> dbTools = factory.manufacturePojo(ArrayList.class, Tool.class);
        dbTools.addAll(ToolMapper.MAPPER.toMo(tools));
        List<CategoryDto> brands = factory.manufacturePojo(ArrayList.class, CategoryDto.class);
        brands.addAll(tools.stream().map(ToolDto::getBrand).toList());
        CategoryDto brandParent = CategoryDto.builder()
                .parent(null)
                .categories(brands)
                .name("Brands")
                .id(1)
                .build();
        List<CategoryDto> categories = factory.manufacturePojo(ArrayList.class, CategoryDto.class);
        categories.addAll(tools.stream().map(ToolDto::getCategory).toList());
        CategoryDto categoryParent = CategoryDto.builder()
                .parent(null)
                .categories(categories)
                .name("Categories")
                .id(2)
                .build();
        List<LocationDto> locations = factory.manufacturePojo(ArrayList.class, LocationDto.class);
        locations.addAll(tools.stream().map(ToolDto::getLocation).toList());
        List<GroupDto> groups = factory.manufacturePojo(ArrayList.class, GroupDto.class);
        groups.addAll(tools.stream().map(ToolDto::getGroup).toList());

        MultipartFile file = MultipartFileFactory.getFileFromTools(tools);

        doReturn(dbTools).when(toolRepository).findAll();
        doReturn(brandParent).when(categoryService).getCategoryParent(CategoryParentEnum.BRANDS);
        doReturn(categoryParent).when(categoryService).getCategoryParent(CategoryParentEnum.CATEGORIES);
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupsService).getAllGroups();
        List<ToolDto> toolsExcelResponse = service.excelToTools(file);

        assertEquals(tools.size(), toolsExcelResponse.size());
    }

    @Test
    void excelToToolsShouldIndicateWhatCellIsWrong() throws IOException {
        List<ToolDto> tools = factory.manufacturePojo(ArrayList.class, ToolDto.class);
        List<Tool> dbTools = factory.manufacturePojo(ArrayList.class, Tool.class);
        dbTools.addAll(ToolMapper.MAPPER.toMo(tools));
        List<CategoryDto> brands = factory.manufacturePojo(ArrayList.class, CategoryDto.class);
        brands.addAll(tools.stream().map(ToolDto::getBrand).toList());
        CategoryDto brandParent = CategoryDto.builder()
                .parent(null)
                .categories(brands)
                .name("Brands")
                .id(1)
                .build();
        List<CategoryDto> categories = factory.manufacturePojo(ArrayList.class, CategoryDto.class);
        categories.addAll(tools.stream().map(ToolDto::getCategory).toList());
        CategoryDto categoryParent = CategoryDto.builder()
                .parent(null)
                .categories(categories)
                .name("Categories")
                .id(2)
                .build();
        List<LocationDto> locations = factory.manufacturePojo(ArrayList.class, LocationDto.class);
        locations.addAll(tools.stream().map(ToolDto::getLocation).toList());
        List<GroupDto> groups = factory.manufacturePojo(ArrayList.class, GroupDto.class);
        groups.addAll(tools.stream().map(ToolDto::getGroup).toList());

        MultipartFile file = MultipartFileFactory.getFileFromToolsIncorrectBarcodeType(tools);


        doReturn(dbTools).when(toolRepository).findAll();
        doReturn(brandParent).when(categoryService).getCategoryParent(CategoryParentEnum.BRANDS);
        doReturn(categoryParent).when(categoryService).getCategoryParent(CategoryParentEnum.CATEGORIES);
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupsService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_CELL_TYPE_INCORRECT.formatted(1, 0, CellType.STRING.toString()), requestException.getMessage());
    }
    @Test
    void excelToToolsShouldIndicateWhatValueIsWrong() throws IOException {
        List<ToolDto> tools = factory.manufacturePojo(ArrayList.class, ToolDto.class);
        List<Tool> dbTools = factory.manufacturePojo(ArrayList.class, Tool.class);
        dbTools.addAll(ToolMapper.MAPPER.toMo(tools));
        List<CategoryDto> brands = factory.manufacturePojo(ArrayList.class, CategoryDto.class);
        brands.addAll(tools.stream().map(ToolDto::getBrand).toList());
        CategoryDto brandParent = CategoryDto.builder()
                .parent(null)
                .categories(brands)
                .name("Brands")
                .id(1)
                .build();
        List<CategoryDto> categories = factory.manufacturePojo(ArrayList.class, CategoryDto.class);
        categories.addAll(tools.stream().map(ToolDto::getCategory).toList());
        CategoryDto categoryParent = CategoryDto.builder()
                .parent(null)
                .categories(categories)
                .name("Categories")
                .id(2)
                .build();
        List<LocationDto> locations = factory.manufacturePojo(ArrayList.class, LocationDto.class);
        locations.addAll(tools.stream().map(ToolDto::getLocation).toList());
        List<GroupDto> groups = factory.manufacturePojo(ArrayList.class, GroupDto.class);
        groups.addAll(tools.stream().map(ToolDto::getGroup).toList());

        MultipartFile file = MultipartFileFactory.getFileFromToolsIncorrectBrandType(tools);


        doReturn(dbTools).when(toolRepository).findAll();
        doReturn(brandParent).when(categoryService).getCategoryParent(CategoryParentEnum.BRANDS);
        doReturn(categoryParent).when(categoryService).getCategoryParent(CategoryParentEnum.CATEGORIES);
        doReturn(locations).when(locationService).getAllLocations();
        doReturn(groups).when(groupsService).getAllGroups();
        RequestException requestException = assertThrows(RequestException.class, () -> service.excelToTools(file));

        assertEquals(Messages.Error.EXCEL_VALUE_INCORRECT.formatted("made up brand", 1, 2).concat("\n").concat(Messages.Error.CATEGORY_SON_NOT_FOUND
                .formatted(CategoryParentEnum.BRANDS.getName(), "made up brand", CategoryParentEnum.BRANDS.getName(), brands.stream().sorted(Comparator.comparing(CategoryDto::getName)).map(CategoryDto::getName).toList().toString())),
                requestException.getMessage());
    }
}
