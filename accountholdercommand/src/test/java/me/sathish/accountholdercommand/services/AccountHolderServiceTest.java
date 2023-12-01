package me.sathish.accountholdercommand.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.willDoNothing;

import java.util.List;
import java.util.Optional;
import me.sathish.accountholdercommand.entities.AccountHolder;
import me.sathish.accountholdercommand.model.response.PagedResult;
import me.sathish.accountholdercommand.repositories.AccountHolderRepository;
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
class AccountHolderServiceTest {

    @Mock private AccountHolderRepository accountHolderRepository;

    @InjectMocks private AccountHolderService accountHolderService;

    @Test
    void findAllAccountHolders() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        Page<AccountHolder> accountHolderPage = new PageImpl<>(List.of(getAccountHolder()));
        given(accountHolderRepository.findAll(pageable)).willReturn(accountHolderPage);

        // when
        PagedResult<AccountHolder> pagedResult =
                accountHolderService.findAllAccountHolders(0, 10, "id", "asc");

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
    void findAccountHolderById() {
        // given
        given(accountHolderRepository.findById(1L)).willReturn(Optional.of(getAccountHolder()));
        // when
        Optional<AccountHolder> optionalAccountHolder =
                accountHolderService.findAccountHolderById(1L);
        // then
        assertThat(optionalAccountHolder).isPresent();
        AccountHolder accountHolder = optionalAccountHolder.get();
        assertThat(accountHolder.getId()).isEqualTo(1L);
        assertThat(accountHolder.getText()).isEqualTo("junitTest");
    }

    @Test
    void saveAccountHolder() {
        // given
        given(accountHolderRepository.save(getAccountHolder())).willReturn(getAccountHolder());
        // when
        AccountHolder persistedAccountHolder =
                accountHolderService.saveAccountHolder(getAccountHolder());
        // then
        assertThat(persistedAccountHolder).isNotNull();
        assertThat(persistedAccountHolder.getId()).isEqualTo(1L);
        assertThat(persistedAccountHolder.getText()).isEqualTo("junitTest");
    }

    @Test
    void deleteAccountHolderById() {
        // given
        willDoNothing().given(accountHolderRepository).deleteById(1L);
        // when
        accountHolderService.deleteAccountHolderById(1L);
        // then
        verify(accountHolderRepository, times(1)).deleteById(1L);
    }

    private AccountHolder getAccountHolder() {
        AccountHolder accountHolder = new AccountHolder();
        accountHolder.setId(1L);
        accountHolder.setText("junitTest");
        return accountHolder;
    }
}
