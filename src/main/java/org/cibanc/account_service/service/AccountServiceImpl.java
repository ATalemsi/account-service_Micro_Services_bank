package org.cibanc.account_service.service;

import lombok.AllArgsConstructor;
import org.cibanc.account_service.dto.AccountDTO;
import org.cibanc.account_service.mapper.AccountMapper;
import org.cibanc.account_service.model.Account;
import org.cibanc.account_service.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final RestTemplate restTemplate;

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);

        if (!clientExists(account.getClientId())) {
            throw new IllegalArgumentException("Le client avec ID " + account.getClientId() + " n'existe pas");
        }

        if (accountRepository.existsByClientIdAndType(account.getClientId(), account.getType())) {
            throw new IllegalArgumentException("Un client ne peut avoir qu'un seul account de type " + account.getType());
        }

        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDTO(savedAccount);
    }

    @Override
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account non trouvé"));

        return accountMapper.toDTO(account);
    }

    @Override
    public List<AccountDTO> getAccountsByClientId(Long clientId) {
        List<Account> accounts = accountRepository.findByClientId(clientId);

        return accounts.stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    public boolean clientExists(Long clientId) {
        String url = "http://customer-service/customers/" + clientId;
        try {
            restTemplate.getForObject(url, Void.class); // Appel REST à customer-service
            return true;
        } catch (Exception e) {
            return false; // Si une exception est levée, le client n'existe pas
        }
    }
}
