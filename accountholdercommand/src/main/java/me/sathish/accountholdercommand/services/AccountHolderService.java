package me.sathish.accountholdercommand.services;

import java.util.Optional;
import me.sathish.accountholdercommand.entities.AccountHolder;
import me.sathish.accountholdercommand.model.response.PagedResult;
import me.sathish.accountholdercommand.repositories.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountHolderService {

    private final AccountHolderRepository accountHolderRepository;

    @Autowired
    public AccountHolderService(AccountHolderRepository accountHolderRepository) {
        this.accountHolderRepository = accountHolderRepository;
    }

    public PagedResult<AccountHolder> findAllAccountHolders(
            int pageNo, int pageSize, String sortBy, String sortDir) {
        Sort sort =
                sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending();

        // create Pageable instance
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<AccountHolder> accountHoldersPage = accountHolderRepository.findAll(pageable);

        return new PagedResult<>(accountHoldersPage);
    }

    public Optional<AccountHolder> findAccountHolderById(Long id) {
        return accountHolderRepository.findById(id);
    }

    public AccountHolder saveAccountHolder(AccountHolder accountHolder) {
        return accountHolderRepository.save(accountHolder);
    }

    public void deleteAccountHolderById(Long id) {
        accountHolderRepository.deleteById(id);
    }
}
