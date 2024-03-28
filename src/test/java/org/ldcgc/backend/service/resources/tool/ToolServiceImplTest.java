package org.ldcgc.backend.service.resources.tool;

import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.model.category.ResourceType;
import org.ldcgc.backend.db.model.group.Group;
import org.ldcgc.backend.db.model.location.Location;
import org.ldcgc.backend.db.model.resources.Tool;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.db.repository.category.ResourceTypeRepository;
import org.ldcgc.backend.db.repository.group.GroupRepository;
import org.ldcgc.backend.db.repository.location.LocationRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.other.PaginationDetails;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.dto.resources.ToolDto;
import org.ldcgc.backend.payload.mapper.resources.tool.ToolMapper;
import org.ldcgc.backend.service.resources.tool.impl.ToolServiceImpl;
import org.ldcgc.backend.strategy.MultipartFileFactory;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomToolDto;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class ToolServiceImplTest {

    @InjectMocks private ToolServiceImpl toolService;
    @Mock private ToolRepository toolRepository;
    @Mock private ToolExcelService toolExcelService;
    @Mock private BrandRepository brandRepository;
    @Mock private ResourceTypeRepository resourceTypeRepository;
    @Mock private LocationRepository locationRepository;
    @Mock private GroupRepository groupRepository;

    private final PodamFactory factory = new PodamFactoryImpl();

    @Test
    void getToolShouldReturnResponseEntity() {
        Tool tool = factory.manufacturePojo(Tool.class);
        ToolDto toolDto = ToolMapper.MAPPER.toDto(tool);

        doReturn(Optional.of(tool)).when(toolRepository).findById(tool.getId());

        ResponseEntity<?> response = toolService.getTool(tool.getId());

        assertNotNull(response);
        verify(toolRepository, times(1)).findById(tool.getId());
        Response.DTO responseBody = (Response.DTO) Objects.requireNonNull(response.getBody());
        assertThat(toolDto).usingRecursiveComparison().isEqualTo(responseBody.getData());
        assertEquals(ToolDto.class, responseBody.getData().getClass());
    }

    @Test
    void getToolShouldThrowExceptionWhenToolNotFound(){
        Integer id = 1;

        doReturn(Optional.empty()).when(toolRepository).findById(id);

        RequestException requestException = assertThrows(RequestException.class, () -> toolService.getTool(id));

        assertEquals(HttpStatus.NOT_FOUND, requestException.getHttpStatus());
        assertTrue(requestException.getMessage().contains(String.format(Messages.Error.TOOL_NOT_FOUND, id)));
    }

    @Test
    void createToolShouldReturnResponseEntity() {
        ToolDto toolDto = getRandomToolDto().toBuilder().id(null).build();
        Tool entityTool = ToolMapper.MAPPER.toMo(toolDto);
        Brand brand = factory.manufacturePojo(Brand.class);
        ResourceType resourceType = factory.manufacturePojo(ResourceType.class);
        Location location = factory.manufacturePojo(Location.class);
        Group group = factory.manufacturePojo(Group.class);

        doReturn(Optional.empty()).when(toolRepository).findFirstByBarcode(toolDto.getBarcode());
        doReturn(Optional.of(brand)).when(brandRepository).findById(toolDto.getBrand().getId());
        doReturn(Optional.of(resourceType)).when(resourceTypeRepository).findById(toolDto.getResourceType().getId());
        doReturn(Optional.of(location)).when(locationRepository).findById(anyInt());
        doReturn(Optional.of(group)).when(groupRepository).findById(anyInt());
        doReturn(entityTool).when(toolRepository).saveAndFlush(any(Tool.class));

        ResponseEntity<?> response = toolService.createTool(toolDto);

        verify(toolRepository, times(1)).findFirstByBarcode(toolDto.getBarcode());
        verify(toolRepository, times(1)).saveAndFlush(any(Tool.class));
        assertNotNull(response);
        Response.DTO responseBody = (Response.DTO) Objects.requireNonNull(response.getBody());
        assertEquals(ToolDto.class, responseBody.getData().getClass());
    }

    @Test
    void createToolShouldThrowExceptionWhenIdIsPresent(){
        ToolDto toolDto = factory.manufacturePojo(ToolDto.class);

        RequestException requestException = assertThrows(RequestException.class, () -> toolService.createTool(toolDto));

        assertEquals(HttpStatus.BAD_REQUEST, requestException.getHttpStatus());
        assertTrue(requestException.getMessage().contains(Messages.Error.TOOL_ID_SHOULDNT_BE_PRESENT));
    }

    @Test
    void createToolShouldThrowExceptionWhenBarcodeIsRepeated(){
        ToolDto toolDto = ToolDto.builder().barcode("1").build();
        Tool tool = ToolMapper.MAPPER.toMo(toolDto);

        doReturn(Optional.of(tool)).when(toolRepository).findFirstByBarcode(toolDto.getBarcode());

        RequestException requestException = assertThrows(RequestException.class, () -> toolService.createTool(toolDto));

        verify(toolRepository, times(1)).findFirstByBarcode(toolDto.getBarcode());
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getHttpStatus());
        assertTrue(requestException.getMessage().contains(String.format(Messages.Error.TOOL_BARCODE_ALREADY_EXISTS, toolDto.getBarcode())));
    }

    @Test
    void putToolShouldReturnResponseEntity() {
        ToolDto toolDto = factory.manufacturePojo(ToolDto.class);
        Tool tool = ToolMapper.MAPPER.toMo(toolDto);
        Brand brand = factory.manufacturePojo(Brand.class);
        ResourceType resourceType = factory.manufacturePojo(ResourceType.class);
        Location location = factory.manufacturePojo(Location.class);
        Group group = factory.manufacturePojo(Group.class);

        doReturn(Optional.of(tool)).when(toolRepository).findById(toolDto.getId());
        doReturn(Optional.empty()).when(toolRepository).findFirstByBarcode(toolDto.getBarcode());
        doReturn(Optional.of(brand)).when(brandRepository).findById(toolDto.getBrand().getId());
        doReturn(Optional.of(resourceType)).when(resourceTypeRepository).findById(toolDto.getResourceType().getId());
        doReturn(Optional.of(location)).when(locationRepository).findById(anyInt());
        doReturn(Optional.of(group)).when(groupRepository).findById(anyInt());
        doReturn(tool).when(toolRepository).saveAndFlush(any(Tool.class));

        ResponseEntity<?> response = toolService.updateTool(toolDto.getId(), toolDto);

        verify(toolRepository, times(1)).findById(toolDto.getId());
        verify(toolRepository, times(1)).findFirstByBarcode(toolDto.getBarcode());
        verify(toolRepository, times(1)).saveAndFlush(tool);
        assertNotNull(response);
        Response.DTO responseBody = (Response.DTO) Objects.requireNonNull(response.getBody());
        assertEquals(ToolDto.class, responseBody.getData().getClass());
    }

    @Test
    void putToolShouldThrowExceptionWhenToolNotFound(){
        Integer id = 1;
        ToolDto toolDto = ToolDto.builder().build();

        doReturn(Optional.empty()).when(toolRepository).findById(id);
        RequestException requestException = assertThrows(RequestException.class, () -> toolService.updateTool(id, toolDto));

        verify(toolRepository, times(1)).findById(id);
        assertEquals(HttpStatus.NOT_FOUND, requestException.getHttpStatus());
        assertTrue(requestException.getMessage().contains(String.format(Messages.Error.TOOL_NOT_FOUND, id)));
    }
    @Test
    void putToolShouldThrowExceptionWhenBarcodeAlreadyExist(){
        Integer id = 1;
        ToolDto toolDto = ToolDto.builder().id(1).barcode("1").build();
        Tool repeatedBarcode = Tool.builder().id(2).barcode("1").build();
        Tool tool = ToolMapper.MAPPER.toMo(toolDto);

        doReturn(Optional.of(tool)).when(toolRepository).findById(id);
        doReturn(Optional.of(repeatedBarcode)).when(toolRepository).findFirstByBarcode(toolDto.getBarcode());

        RequestException requestException = assertThrows(RequestException.class, () -> toolService.updateTool(id, toolDto));

        verify(toolRepository, times(1)).findById(id);
        verify(toolRepository, times(1)).findFirstByBarcode(toolDto.getBarcode());
        assertEquals(HttpStatus.BAD_REQUEST, requestException.getHttpStatus());
        assertTrue(requestException.getMessage().contains(String.format(Messages.Error.TOOL_BARCODE_ALREADY_EXISTS, toolDto.getBarcode())));

    }

    @Test
    void deleteToolShouldReturnResponseEntity() {
        Tool tool = factory.manufacturePojo(Tool.class);
        Integer toolId = tool.getId();

        doReturn(Optional.of(tool)).when(toolRepository).findById(toolId);

        ResponseEntity<?> response = toolService.deleteTool(toolId);

        verify(toolRepository, times(1)).findById(toolId);
        verify(toolRepository, times(1)).delete(tool);

        assertNotNull(response);
        Response.DTO responseBody = (Response.DTO) Objects.requireNonNull(response.getBody());
        assertEquals(Messages.Info.TOOL_DELETED, responseBody.getMessage());
    }

    @Test
    void deleteToolShouldThrowExceptionWhenToolNotFound() {
        Integer id = 1;

        doReturn(Optional.empty()).when(toolRepository).findById(id);

        RequestException requestException = assertThrows(RequestException.class, () -> toolService.deleteTool(id));

        verify(toolRepository, times(1)).findById(id);

        assertEquals(HttpStatus.NOT_FOUND, requestException.getHttpStatus());
        assertTrue(requestException.getMessage().contains(String.format(Messages.Error.TOOL_NOT_FOUND, id)));
    }


    @Test
    void getAllToolsShouldReturnPage() {
        String sortString = "name";

        Page<Tool> tools = new PageImpl<>(factory.manufacturePojo(List.class, Tool.class));

        doReturn(tools).when(toolRepository).findAllFiltered(eq(""), eq(""), eq(""), eq(""), eq(""), eq(null), any(Pageable.class));

        ResponseEntity<?> response = toolService.getAllTools(0, 25, "", "", "", "", "", null, sortString);

        verify(toolRepository, atMostOnce()).findAllFiltered(eq(""), eq(""), eq(""), eq(""), eq(""), eq(null), any(Pageable.class));

        assertNotNull(response);
        Response.DTO responseBody = (Response.DTO) Objects.requireNonNull(response.getBody());
        assertEquals(ToolDto.class, ((PaginationDetails) responseBody.getData()).getElements().getFirst().getClass());
    }

    @Test
    void getAllToolsShouldThrowExceptionWhenStatusNotFound(){
        String status = "made up status";
        String sorField = "name";

        RequestException requestException = assertThrows(RequestException.class, () -> toolService.getAllTools(0, 25, "", "", "", "", "", status, sorField));

        assertEquals(HttpStatus.NOT_FOUND, requestException.getHttpStatus());
        assertTrue(requestException.getMessage().contains(String.format(Messages.Error.STATUS_NOT_FOUND, status)));
    }
    @Test
    void uploadToolsExcelShouldReturnList() throws IOException {
        List<ToolDto> tools = factory.manufacturePojo(ArrayList.class, ToolDto.class);
        List<Tool> toolEntities = factory.manufacturePojo(ArrayList.class, Tool.class);
        MultipartFile file = MultipartFileFactory.getFileFromTools(tools, null);

        doReturn(tools).when(toolExcelService).excelToTools(file);
        doReturn(toolEntities).when(toolRepository).saveAll(any());

        ResponseEntity<?> response = toolService.uploadToolsExcel(file);

        verify(toolExcelService, times(1)).excelToTools(any());
        verify(toolRepository, times(1)).saveAll(any());

        assertNotNull(response);
        Response.DTO responseBody = ((Response.DTO) response.getBody());
        assertNotNull(responseBody);
        assertEquals(ToolDto.class, ((List<ToolDto>) responseBody.getData()).getFirst().getClass());
    }
}
