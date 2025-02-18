package org.cibanc.account_service.service;

import org.cibanc.account_service.dto.AccountDTO;


import java.util.List;


public interface AccountService {
    AccountDTO createAccount(AccountDTO accountDTO);

    AccountDTO getAccountById(Long id);

    List<AccountDTO> getAccountsByClientId(Long clientId);
}
