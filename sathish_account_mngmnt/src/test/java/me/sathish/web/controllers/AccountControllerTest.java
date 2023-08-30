package me.sathish.web.controllers;

import static me.sathish.utils.AppConstants.PROFILE_TEST;
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
import me.sathish.entities.Account;
import me.sathish.model.response.PagedResult;
import me.sathish.services.AccountService;
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

@WebMvcTest(controllers = AccountController.class)
@ActiveProfiles(PROFILE_TEST)
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AccountService accountService;

    @Autowired private ObjectMapper objectMapper;

    private List<Account> accountList;

    @BeforeEach
    void setUp() {
        this.accountList = new ArrayList<>();
        this.accountList.add(new Account(1L, "text 1"));
        this.accountList.add(new Account(2L, "text 2"));
        this.accountList.add(new Account(3L, "text 3"));
    }

    @Test
    void shouldFetchAllAccounts() throws Exception {
        Page<Account> page = new PageImpl<>(accountList);
        PagedResult<Account> accountPagedResult = new PagedResult<>(page);
        given(accountService.findAllAccounts(0, 10, "id", "asc")).willReturn(accountPagedResult);

        this.mockMvc
                .perform(get("/api/account"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()", is(accountList.size())))
                .andExpect(jsonPath("$.totalElements", is(3)))
                .andExpect(jsonPath("$.pageNumber", is(1)))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.isFirst", is(true)))
                .andExpect(jsonPath("$.isLast", is(true)))
                .andExpect(jsonPath("$.hasNext", is(false)))
                .andExpect(jsonPath("$.hasPrevious", is(false)));
    }

    @Test
    void shouldFindAccountById() throws Exception {
        Long accountId = 1L;
        Account account = new Account(accountId, "text 1");
        given(accountService.findAccountById(accountId)).willReturn(Optional.of(account));

        this.mockMvc
                .perform(get("/api/account/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(account.getText())));
    }

    @Test
    void shouldReturn404WhenFetchingNonExistingAccount() throws Exception {
        Long accountId = 1L;
        given(accountService.findAccountById(accountId)).willReturn(Optional.empty());

        this.mockMvc.perform(get("/api/account/{id}", accountId)).andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateNewAccount() throws Exception {
        given(accountService.saveAccount(any(Account.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        Account account = new Account(1L, "some text");
        this.mockMvc
                .perform(
                        post("/api/account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.text", is(account.getText())));
    }

    @Test
    void shouldReturn400WhenCreateNewAccountWithoutText() throws Exception {
        Account account = new Account(null, null);

        this.mockMvc
                .perform(
                        post("/api/account")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", is("application/problem+json")))
                .andExpect(jsonPath("$.type", is("about:blank")))
                .andExpect(jsonPath("$.title", is("Constraint Violation")))
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.detail", is("Invalid request content.")))
                .andExpect(jsonPath("$.instance", is("/api/account")))
                .andExpect(jsonPath("$.violations", hasSize(1)))
                .andExpect(jsonPath("$.violations[0].field", is("text")))
                .andExpect(jsonPath("$.violations[0].message", is("Text cannot be empty")))
                .andReturn();
    }

    @Test
    void shouldUpdateAccount() throws Exception {
        Long accountId = 1L;
        Account account = new Account(accountId, "Updated text");
        given(accountService.findAccountById(accountId)).willReturn(Optional.of(account));
        given(accountService.saveAccount(any(Account.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        this.mockMvc
                .perform(
                        put("/api/account/{id}", account.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(account.getText())));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingAccount() throws Exception {
        Long accountId = 1L;
        given(accountService.findAccountById(accountId)).willReturn(Optional.empty());
        Account account = new Account(accountId, "Updated text");

        this.mockMvc
                .perform(
                        put("/api/account/{id}", accountId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAccount() throws Exception {
        Long accountId = 1L;
        Account account = new Account(accountId, "Some text");
        given(accountService.findAccountById(accountId)).willReturn(Optional.of(account));
        doNothing().when(accountService).deleteAccountById(account.getId());

        this.mockMvc
                .perform(delete("/api/account/{id}", account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(account.getText())));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingAccount() throws Exception {
        Long accountId = 1L;
        given(accountService.findAccountById(accountId)).willReturn(Optional.empty());

        this.mockMvc
                .perform(delete("/api/account/{id}", accountId))
                .andExpect(status().isNotFound());
    }
}
