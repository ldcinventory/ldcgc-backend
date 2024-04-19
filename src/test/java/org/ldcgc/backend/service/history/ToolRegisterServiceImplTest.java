package org.ldcgc.backend.service.history;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.ldcgc.backend.db.repository.history.ToolRegisterRepository;
import org.ldcgc.backend.db.repository.resources.ToolRepository;
import org.ldcgc.backend.db.repository.users.VolunteerRepository;
import org.ldcgc.backend.service.history.impl.ToolRegisterServiceImpl;
import org.ldcgc.backend.service.resources.tool.impl.ToolServiceImpl;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Fail.fail;
import static org.ldcgc.backend.base.Constants.NOT_YET_IMPLEMENTED;

@ExtendWith(MockitoExtension.class)
public class ToolRegisterServiceImplTest {

    // service
    private ToolRegisterService toolRegisterService;

    // repository
    @Mock private ToolRegisterRepository toolRegisterRepository;
    @Mock private VolunteerRepository volunteerRepository;
    @Mock private ToolRepository toolRepository;
    @Mock private ToolServiceImpl toolService;

    @BeforeEach
    void init() {
        toolRegisterService = new ToolRegisterServiceImpl(toolRegisterRepository, volunteerRepository, toolRepository, toolService);
    }

    // CREATE

    @Test
    void whenCreateToolRegister_returnToolRegisterVolunteerNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateToolRegister_returnToolRegisterTooManyVolunteers() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateToolRegister_returnToolRegisterNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateToolRegister_returnToolRegisterToolNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateToolRegister_returnToolRegisterNotAvailable() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateToolRegister_returnToolRegisterCreated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // GET ALL

    @Test
    void whenGetAllToolRegisters_returnPageIndexRequestedExceededTotal() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenGetAllToolRegistersUnfiltered_returnPagedResults() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenGetAllToolRegistersFiltered_returnPagedResults() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // UPDATE

    @Test
    void whenUpdateRegister_returnToolToolRegisterNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateRegister_returnToolToolRegisterIncorrectBarcode() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateRegister_returnToolToolRegisterIncorrectBuilderAssistantId() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenUpdateRegister_returnToolToolRegisterUpdated() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // GET

    @Test
    void whenGetRegister_returnToolToolRegisterNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenGetRegister_returnToolToolRegister() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // DELETE

    @Test
    void whenDeleteRegister_returnToolToolRegisterNotFound() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenDeleteRegister_returnToolToolRegisterDeleted() {
        fail(NOT_YET_IMPLEMENTED);
    }

    // CREATE MULTIPLE

    @Test
    void whenCreateMultipleToolRegisters_returnToolRegisterRepeatedTools() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateMultipleToolRegisters_returnToolNotFoundBarcode() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateMultipleToolRegisters_returnToolRegisterToolNotAvailable() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateMultipleToolRegisters_returnVolunteerNotFoundBAID() {
        fail(NOT_YET_IMPLEMENTED);
    }

    @Test
    void whenCreateMultipleToolRegisters_returnOK() {
        fail(NOT_YET_IMPLEMENTED);
    }

}
