package org.ldcgc.backend.service.resources.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.db.model.category.Brand;
import org.ldcgc.backend.db.repository.category.BrandRepository;
import org.ldcgc.backend.exception.RequestException;
import org.ldcgc.backend.payload.dto.category.BrandDto;
import org.ldcgc.backend.payload.dto.other.NonPaged;
import org.ldcgc.backend.payload.dto.other.Response;
import org.ldcgc.backend.payload.mapper.category.BrandMapper;
import org.ldcgc.backend.service.resources.common.impl.BrandServiceImpl;
import org.ldcgc.backend.util.constants.Messages;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomBrand;
import static org.ldcgc.backend.base.mock.MockedResources.getRandomIndexFromList;
import static org.ldcgc.backend.base.mock.MockedResources.listRandomResource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
class BrandServiceImplTest {

    @InjectMocks private BrandServiceImpl brandService;

    @Mock private BrandRepository brandRepository;

    List<BrandDto> brandsDto;

    @BeforeEach
    void init() {
        brandsDto = listRandomResource(BrandDto.class, null);
    }

    // list all
    @Test
    void whenGetBrands_ReturnEmptyList() {
        doReturn(Collections.emptyList()).when(brandRepository).findAll();

        ResponseEntity<?> response = brandService.getBrands();
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody);
        NonPaged brandsResponse = (NonPaged) responseBody.getData();
        assertEquals(brandsResponse.getElements(), Collections.emptyList());
    }

    @Test
    void whenGetBrands_ReturnList() {
        List<Brand> brandEntities = brandsDto.stream().map(BrandMapper.MAPPER::toEntity).toList();
        doReturn(brandEntities).when(brandRepository).findAll();

        int randomIndex = getRandomIndexFromList(brandEntities);

        ResponseEntity<?> response = brandService.getBrands();
        Response.DTO responseBody = (Response.DTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody);
        NonPaged brandsResponse = (NonPaged) responseBody.getData();
        assertEquals(brandsResponse.getElements().size(), brandEntities.size());
        assertThat(brandsResponse.getElements().get(randomIndex))
            .usingRecursiveComparison().isEqualTo(brandEntities.get(randomIndex));
        assertThat(brandsResponse.getElements()).usingRecursiveFieldByFieldElementComparator().isEqualTo(brandEntities);

        verify(brandRepository, atMostOnce()).findAll();
    }

    // create
    @Test
    void whenCreateBrands_ReturnBrandsExists() {
        doReturn(true).when(brandRepository).existsByName(anyString());

        RequestException ex = assertThrows(RequestException.class, () -> brandService.createBrand(getRandomBrand()));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
        assertEquals(Messages.Error.BRAND_EXISTS, ex.getMessage());

        verify(brandRepository, atMostOnce()).existsByName(anyString());
    }

    @Test
    void whenCreateBrands_ReturnBrandsCreated() {
        doReturn(false).when(brandRepository).existsByName(anyString());
        BrandDto brandDto = getRandomBrand();
        doReturn(BrandMapper.MAPPER.toEntity(brandDto)).when(brandRepository).save(any(Brand.class));

        ResponseEntity<?> response = brandService.createBrand(brandDto);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Response.DTO body = (Response.DTO) response.getBody();
        assertEquals(Messages.Info.BRAND_CREATED, Objects.requireNonNull(body).getMessage());
        assertThat(brandDto).usingRecursiveComparison().isEqualTo(body.getData());
    }

    // delete
    @Test
    void whenDeleteBrands_ReturnBrandsNotFound() {
        doReturn(Optional.empty()).when(brandRepository).findById(anyInt());

        RequestException ex = assertThrows(RequestException.class, () -> brandService.deleteBrand(0));

        assertEquals(HttpStatus.NOT_FOUND, ex.getHttpStatus());
        assertEquals(String.format(Messages.Error.BRAND_NOT_FOUND, 0), ex.getMessage());

        verify(brandRepository, atMostOnce()).findById(anyInt());
    }

    @Test
    void whenDeleteBrands_ReturnBrandsLocked() {
        doReturn(Optional.of(BrandMapper.MAPPER.toEntity(getRandomBrand(true)))).when(brandRepository).findById(anyInt());

        RequestException ex = assertThrows(RequestException.class, () -> brandService.deleteBrand(0));

        assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatus());
        assertEquals(String.format(Messages.Error.BRAND_LOCKED, 0), ex.getMessage());

        verify(brandRepository, atMostOnce()).findById(anyInt());
    }

    @Test
    void whenDeleteBrands_ReturnBrandsDeleted() {
        doReturn(Optional.of(BrandMapper.MAPPER.toEntity(getRandomBrand(false)))).when(brandRepository).findById(anyInt());

        ResponseEntity<?> response = brandService.deleteBrand(0);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Response.DTO body = (Response.DTO) response.getBody();
        assertEquals(String.format(Messages.Info.BRAND_DELETED, 0), Objects.requireNonNull(body).getMessage());
    }

}
