package org.ldcgc.backend.service.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ldcgc.backend.db.repository.history.ConsumableRegisterRepository;
import org.ldcgc.backend.db.repository.resources.ConsumableRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.service.history.impl.ConsumableRegisterServiceImpl;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
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
    }

    @Test
    void whenGetConsumableRegister_returnConsumableRegister() {
    }

    //LIST
    @Test
    void whenListConsumableRegisterWithoutFilters_returnPageIndexRequestedExceededTotal() {
    }

    @Test
    void whenListConsumableRegisterWithoutFilters_returnConsumableRegisters() {
    }

    @Test
    void whenListConsumableRegisterFilteredByBuilderAssistantId_returnConsumableRegisters() {
    }

    @Test
    void whenListConsumableRegisterFilteredByConsumableBarcode_returnConsumableRegisters() {
    }

    @Test
    void whenListConsumableRegisterFilteredByRangeOfDates_returnConsumableRegisters() {
    }

    //CREATE
    @Test
    void whenCreateConsumableRegisterWithoutRegistrationOut_returnConsumableRegisterVolunteerDuplicated() {
    }

    @Test
    void whenCreateConsumableRegisterWithoutRegistrationOut_returnConsumableRegisterNotEnoughAmmountAllocate() {
    }

    @Test
    void whenCreateConsumableRegisterWithoutRegistrationOut_returnConsumableRegisterAllocateBeforeToday() {
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterReturnDateBeforeAllocate() {
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterNotEnoughAmountAllocate() {
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterReturnDateAfterToday() {
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterDataOutNotComplete() {
    }

    @Test
    void whenCreateOpenConsumableRegister_returnConsumableRegisterDataClosingNotComplete() {
    }

    @Test
    void whenCreateClosedConsumableRegister_returnConsumableRegisterDataClosingNotComplete() {
    }

    @Test
    void whenCreateConsumableRegister_returnConsumableRegisterCreated() {
    }

    //UPDATE
    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterClosedForModifications() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterReturnDateBeforeAllocate() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterReturnDateAfterToday() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterNotEnoughAmountAllocate() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterNotEnoughAmountReturn() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterReplacingStockValues() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterUpdatingNonUsedStock() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterClosingRegister() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegisterDataClosingNotComplete() {
    }

    @Test
    void whenUpdateConsumableRegister_returnConsumableRegister() {
    }

    //DELETE
    @Test
    void whenDeleteConsumableRegister_returnConsumableRegisterNotFound() {
    }

    @Test
    void whenDeleteConsumableRegister_returnConsumableRegisterDeletedWithUndoStockChanges() {
    }

    @Test
    void whenDeleteConsumableRegister_returnConsumableRegisterDeleted() {
    }
}
