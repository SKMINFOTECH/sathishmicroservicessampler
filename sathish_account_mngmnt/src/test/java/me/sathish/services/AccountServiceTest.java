package me.sathish.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

import java.util.List;
import java.util.Optional;
import me.sathish.entities.Account;
import me.sathish.model.response.PagedResult;
import me.sathish.repositories.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock private AccountRepository accountRepository;

    @InjectMocks private AccountService accountService;

    @Test
    void findAllAccounts() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<Account> accountPage = new PageImpl<>(List.of(getAccount()));
        given(accountRepository.findAll(pageable)).willReturn(accountPage);

        // when
        PagedResult<Account> pagedResult = accountService.findAllAccounts(0, 10, "id", "asc");

        // then
        assertThat(pagedResult).isNotNull();
        assertThat(pagedResult.data()).isNotEmpty().hasSize(1);
        assertThat(pagedResult.hasNext()).isFalse();
        assertThat(pagedResult.pageNumber()).isEqualTo(1);
        assertThat(pagedResult.totalPages()).isEqualTo(1);
        assertThat(pagedResult.isFirst()).isTrue();
        assertThat(pagedResult.isLast()).isTrue();
        assertThat(pagedResult.hasPrevious()).isFalse();
        assertThat(pagedResult.totalElements()).isEqualTo(1);
    }

    @Test
    void findAccountById() {
        // given
        given(accountRepository.findById(1L)).willReturn(Optional.of(getAccount()));
        // when
        Optional<Account> optionalAccount = accountService.findAccountById(1L);
        // then
        assertThat(optionalAccount).isPresent();
        Account account = optionalAccount.get();
        assertThat(account.getId()).isEqualTo(1L);
        assertThat(account.getText()).isEqualTo("junitTest");
    }

    @Test
    void saveAccount() {
        // given
        given(accountRepository.save(getAccount())).willReturn(getAccount());
        // when
        Account persistedAccount = accountService.saveAccount(getAccount());
        // then
        assertThat(persistedAccount).isNotNull();
        assertThat(persistedAccount.getId()).isEqualTo(1L);
        assertThat(persistedAccount.getText()).isEqualTo("junitTest");
    }

    @Test
    void deleteAccountById() {
        // given
        willDoNothing().given(accountRepository).deleteById(1L);
        // when
        accountService.deleteAccountById(1L);
        // then
        verify(accountRepository, times(1)).deleteById(1L);
    }

    private Account getAccount() {
        Account account = new Account();
        account.setId(1L);
        account.setText("junitTest");
        return account;
    }
}
