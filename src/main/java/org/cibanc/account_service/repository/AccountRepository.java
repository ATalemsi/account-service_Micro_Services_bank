package org.cibanc.account_service.repository;

import org.cibanc.account_service.model.Account;
import org.cibanc.account_service.model.enums.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByClientId(Long clientId);
    boolean existsByClientIdAndType(Long clientId, TypeCompte type);
}
