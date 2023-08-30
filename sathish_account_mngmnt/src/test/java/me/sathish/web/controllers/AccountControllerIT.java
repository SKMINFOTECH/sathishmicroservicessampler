package me.sathish.web.controllers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import me.sathish.common.AbstractIntegrationTest;
import me.sathish.entities.Account;
import me.sathish.repositories.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class AccountControllerIT extends AbstractIntegrationTest {

    @Autowired private AccountRepository accountRepository;

    private List<Account> accountList = null;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAllInBatch();

        accountList = new ArrayList<>();
        accountList.add(new Account(null, "First Account"));
        accountList.add(new Account(null, "Second Account"));
        accountList.add(new Account(null, "Third Account"));
        accountList = accountRepository.saveAll(accountList);
    }

    @Test
    void shouldFetchAllAccounts() throws Exception {
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
        Account account = accountList.get(0);
        Long accountId = account.getId();

        this.mockMvc
                .perform(get("/api/account/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(account.getText())));
    }

    @Test
    void shouldCreateNewAccount() throws Exception {
        Account account = new Account(null, "New Account");
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
        Account account = accountList.get(0);
        account.setText("Updated Account");

        this.mockMvc
                .perform(
                        put("/api/account/{id}", account.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(account)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(account.getText())));
    }

    @Test
    void shouldDeleteAccount() throws Exception {
        Account account = accountList.get(0);

        this.mockMvc
                .perform(delete("/api/account/{id}", account.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(account.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(account.getText())));
    }
}
