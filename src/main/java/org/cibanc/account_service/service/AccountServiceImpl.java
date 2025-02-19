package org.cibanc.account_service.service;

import lombok.AllArgsConstructor;
import org.cibanc.account_service.dto.AccountDTO;
import org.cibanc.account_service.exceptions.ClientServiceException;
import org.cibanc.account_service.exceptions.ResourceNotFoundException;
import org.cibanc.account_service.mapper.AccountMapper;
import org.cibanc.account_service.model.Account;
import org.cibanc.account_service.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final RestTemplate restTemplate;

    @Override
    public AccountDTO createAccount(AccountDTO accountDTO) {
        Account account = accountMapper.toEntity(accountDTO);

        log.info("Checking if client with ID {} exists...", account.getClientId());

        if (!clientExists(account.getClientId())) {
            throw new ResourceNotFoundException("Le client avec ID " + account.getClientId() + " n'existe pas");
        }

        if (accountRepository.existsByClientIdAndType(account.getClientId(), account.getType())) {
            throw new IllegalArgumentException("Un client ne peut avoir qu'un seul compte de type " + account.getType());
        }

        account.setType(accountDTO.getType());
        Account savedAccount = accountRepository.save(account);
        return accountMapper.toDTO(savedAccount);
    }

    @Override
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compte non trouvé"));

        return accountMapper.toDTO(account);
    }

    @Override
    public List<AccountDTO> getAccountsByClientId(Long clientId) {
        List<Account> accounts = accountRepository.findByClientId(clientId);
        if (accounts == null) {
            throw new ResourceNotFoundException("accounts is vide");
        }
        return accounts.stream()
                .map(accountMapper::toDTO)
                .toList();
    }

    public boolean clientExists(Long clientId) {
        String url = "http://localhost:8080/customer-service/customers/" + clientId;
        try {
            ResponseEntity<Void> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Void.class
            );
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Client with ID {} not found.", clientId);
            return false;
        } catch (HttpClientErrorException e) {
            log.error("HTTP error while checking client existence: {}", e.getMessage());
            throw new ClientServiceException("Erreur lors de la vérification du client: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while checking if client exists", e);
            throw new ClientServiceException("Erreur interne lors de la vérification du client.");
        }
    }
}
