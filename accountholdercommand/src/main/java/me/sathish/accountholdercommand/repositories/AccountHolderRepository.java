package me.sathish.accountholdercommand.repositories;

import me.sathish.accountholdercommand.entities.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {}
