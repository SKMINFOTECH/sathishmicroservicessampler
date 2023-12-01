package me.sathish.accountholdercommand.web.controllers;

import static me.sathish.accountholdercommand.utils.AppConstants.PROFILE_TEST;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.sathish.accountholdercommand.entities.AccountHolder;
import me.sathish.accountholdercommand.model.response.PagedResult;
import me.sathish.accountholdercommand.services.AccountHolderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AccountHolderController.class)
@ActiveProfiles(PROFILE_TEST)
class AccountHolderControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AccountHolderService accountHolderService;

    @Autowired private ObjectMapper objectMapper;

    private List<AccountHolder> accountHolderList;

    @BeforeEach
    void setUp() {
        this.accountHolderList = new ArrayList<>();
        this.accountHolderList.add(new AccountHolder(1L, "text 1"));
        this.accountHolderList.add(new AccountHolder(2L, "text 2"));
        this.accountHolderList.add(new AccountHolder(3L, "text 3"));
    }

    @Test
    void shouldFetchAllAccountHolders() throws Exception {
        Page<AccountHolder> page = new PageImpl<>(accountHolderList);
        PagedResult<AccountHolder> accountHolderPagedResult = new PagedResult<>(page);
        given(accountHolderService.findAllAccountHolders(0, 10, "id", "asc"))
                .willReturn(accountHolderPagedResult);

        this.mockMvc
                .perform(get("/api/accounholder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()", is(accountHolderList.size())))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.isFirst", is(true)))
                .andExpect(jsonPath("$.isLast", is(true)))
                .andExpect(jsonPath("$.hasNext", is(false)))
                .andExpect(jsonPath("$.hasPrevious", is(false)));
    }

    @Test
    void shouldFindAccountHolderById() throws Exception {
        Long accountHolderId = 1L;
        AccountHolder accountHolder = new AccountHolder(accountHolderId, "text 1");
        given(accountHolderService.findAccountHolderById(accountHolderId))
                .willReturn(Optional.of(accountHolder));

        this.mockMvc
                .perform(get("/api/accounholder/{id}", accountHolderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(accountHolder.getText())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingAccountHolder() throws Exception {
        Long accountHolderId = 1L;
        given(accountHolderService.findAccountHolderById(accountHolderId))
                .willReturn(Optional.empty());

        this.mockMvc
                .perform(get("/api/accounholder/{id}", accountHolderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewAccountHolder() throws Exception {
        given(accountHolderService.saveAccountHolder(any(AccountHolder.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        AccountHolder accountHolder = new AccountHolder(1L, "some text");
        this.mockMvc
                .perform(
                        post("/api/accounholder")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountHolder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(accountHolder.getText())));
    }

    @Test
    void shouldReturn400WhenCreateNewAccountHolderWithoutText() throws Exception {
        AccountHolder accountHolder = new AccountHolder(null, null);

        this.mockMvc
                .perform(
                        post("/api/accounholder")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountHolder)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/accounholder")))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("text")))
                .andExpect(jsonPath("$.violations[0].message", is("Text cannot be empty")))
                .andReturn();
    }

    @Test
    void shouldUpdateAccountHolder() throws Exception {
        Long accountHolderId = 1L;
        AccountHolder accountHolder = new AccountHolder(accountHolderId, "Updated text");
        given(accountHolderService.findAccountHolderById(accountHolderId))
                .willReturn(Optional.of(accountHolder));
        given(accountHolderService.saveAccountHolder(any(AccountHolder.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/accounholder/{id}", accountHolder.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountHolder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(accountHolder.getText())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingAccountHolder() throws Exception {
        Long accountHolderId = 1L;
        given(accountHolderService.findAccountHolderById(accountHolderId))
                .willReturn(Optional.empty());
        AccountHolder accountHolder = new AccountHolder(accountHolderId, "Updated text");

        this.mockMvc
                .perform(
                        put("/api/accounholder/{id}", accountHolderId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountHolder)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAccountHolder() throws Exception {
        Long accountHolderId = 1L;
        AccountHolder accountHolder = new AccountHolder(accountHolderId, "Some text");
        given(accountHolderService.findAccountHolderById(accountHolderId))
                .willReturn(Optional.of(accountHolder));
        doNothing().when(accountHolderService).deleteAccountHolderById(accountHolder.getId());

        this.mockMvc
                .perform(delete("/api/accounholder/{id}", accountHolder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(accountHolder.getText())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingAccountHolder() throws Exception {
        Long accountHolderId = 1L;
        given(accountHolderService.findAccountHolderById(accountHolderId))
                .willReturn(Optional.empty());

        this.mockMvc
                .perform(delete("/api/accounholder/{id}", accountHolderId))
                .andExpect(status().isNotFound());
    }
}
