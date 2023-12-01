package me.sathish.accountholdercommand.web.controllers;

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
import me.sathish.accountholdercommand.common.AbstractIntegrationTest;
import me.sathish.accountholdercommand.entities.AccountHolder;
import me.sathish.accountholdercommand.repositories.AccountHolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

class AccountHolderControllerIT extends AbstractIntegrationTest {

    @Autowired private AccountHolderRepository accountHolderRepository;

    private List<AccountHolder> accountHolderList = null;

    @BeforeEach
    void setUp() {
        accountHolderRepository.deleteAllInBatch();

        accountHolderList = new ArrayList<>();
        accountHolderList.add(new AccountHolder(null, "First AccountHolder"));
        accountHolderList.add(new AccountHolder(null, "Second AccountHolder"));
        accountHolderList.add(new AccountHolder(null, "Third AccountHolder"));
        accountHolderList = accountHolderRepository.saveAll(accountHolderList);
    }

    @Test
    void shouldFetchAllAccountHolders() throws Exception {
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
        AccountHolder accountHolder = accountHolderList.get(0);
        Long accountHolderId = accountHolder.getId();

        this.mockMvc
                .perform(get("/api/accounholder/{id}", accountHolderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountHolder.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(accountHolder.getText())));
    }

    @Test
    void shouldCreateNewAccountHolder() throws Exception {
        AccountHolder accountHolder = new AccountHolder(null, "New AccountHolder");
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
        AccountHolder accountHolder = accountHolderList.get(0);
        accountHolder.setText("Updated AccountHolder");

        this.mockMvc
                .perform(
                        put("/api/accounholder/{id}", accountHolder.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(accountHolder)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountHolder.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(accountHolder.getText())));
    }

    @Test
    void shouldDeleteAccountHolder() throws Exception {
        AccountHolder accountHolder = accountHolderList.get(0);

        this.mockMvc
                .perform(delete("/api/accounholder/{id}", accountHolder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(accountHolder.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(accountHolder.getText())));
    }
}
