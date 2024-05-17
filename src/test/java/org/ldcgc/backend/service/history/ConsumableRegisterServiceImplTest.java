package org.ldcgc.backend.service.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.base.GlobalTestConfig;
import org.ldcgc.backend.db.repository.history.ConsumableRegisterRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.service.history.impl.ConsumableRegisterServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith({MockitoExtension.class, GlobalTestConfig.class})
public class ConsumableRegisterServiceImplTest {

    // service
    private ConsumableRegisterService consumableRegisterService;

    // repository
    @Mock private ConsumableRegisterRepository consumableRegisterRepository;
    @Mock private ConsumableRepository consumableRepository;
    @Mock private VolunteerRepository volunteerRepository;


    @BeforeEach
    void init() {
        consumableRegisterService = new ConsumableRegisterServiceImpl(consumableRegisterRepository, consumableRepository, volunteerRepository);
    }

    // GET
    @Test
    void whenGetConsumableRegister_returnConsumableRegisterNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenGetConsumableRegister_returnConsumableRegister() {
        fail(NOT_YET_IMPLEMENTED);
    }

    //LIST
    @Test
    void whenListConsumableRegisterWithoutFilters_returnPageIndexRequestedExceededTotal() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumableRegisterWithoutFilters_returnConsumableRegisters() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumableRegisterFilteredByBuilderAssistantId_returnConsumableRegisters() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumableRegisterFilteredByConsumableBarcode_returnConsumableRegisters() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenListConsumableRegisterFilteredByRangeOfDates_returnConsumableRegisters() {
        fail(NOT_YET_IMPLEMENTED);
    }

    //CREATE
    @Test
    void whenCreateConsumableRegisterWithoutRegistrationOut_returnConsumableRegisterVolunteerDuplicated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumableRegisterWithoutRegistrationOut_returnConsumableRegisterNotEnoughAmmountAllocate() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumableRegisterWithoutRegistrationOut_returnConsumableRegisterAllocateBeforeToday() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterReturnDateBeforeAllocate() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterNotEnoughAmountAllocate() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterReturnDateAfterToday() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterDataOutNotComplete() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateOpenConsumableRegister_returnConsumableRegisterDataClosingNotComplete() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateClosedConsumableRegister_returnConsumableRegisterDataClosingNotComplete() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    //UPDATE
    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterClosedForModifications() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterReturnDateBeforeAllocate() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterReturnDateAfterToday() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterNotEnoughAmountAllocate() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterNotEnoughAmountReturn() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterReplacingStockValues() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterUpdatingNonUsedStock() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterClosingRegister() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterDataClosingNotComplete() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegister() {
        fail(NOT_YET_IMPLEMENTED);
    }

    //DELETE
    @Test
    void whenDeleteConsumableRegister_returnConsumableRegisterNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenDeleteConsumableRegister_returnConsumableRegisterDeletedWithUndoStockChanges() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenDeleteConsumableRegister_returnConsumableRegisterDeleted() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // CREATE MULTIPLE
    @Test
    void whenCreateMultipleConsumableRegisters_returnConsumableRegistersNotCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateMultipleConsumableRegisters_returnConsumableRegistersCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
