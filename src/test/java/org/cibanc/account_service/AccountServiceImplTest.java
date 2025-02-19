package org.cibanc.account_service;

import org.cibanc.account_service.dto.AccountDTO;
import org.cibanc.account_service.exceptions.ClientServiceException;
import org.cibanc.account_service.exceptions.ResourceNotFoundException;
import org.cibanc.account_service.mapper.AccountMapper;
import org.cibanc.account_service.model.Account;
import org.cibanc.account_service.model.enums.TypeCompte;
import org.cibanc.account_service.repository.AccountRepository;
import org.cibanc.account_service.service.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        account = new Account();
        account.setId(1L);
        account.setClientId(100L);
        account.setType(TypeCompte.EPARGNE);

        accountDTO = new AccountDTO();
        accountDTO.setId(1L);
        accountDTO.setClientId(100L);
        accountDTO.setType(TypeCompte.EPARGNE);
    }

    @Test
    void testCreateAccount_Success() {
        // Arrange
        when(accountMapper.toEntity(accountDTO)).thenReturn(account);
        when(accountRepository.existsByClientIdAndType(100L, TypeCompte.EPARGNE)).thenReturn(false);
        when(accountRepository.save(account)).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        // Mocking exchange method to return a valid ResponseEntity
        String url = "http://localhost:8080/customer-service/customers/100";
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build()); // Mock a valid response

        // Act
        AccountDTO result = accountService.createAccount(accountDTO);

        // Assert
        assertNotNull(result);
        assertEquals(TypeCompte.EPARGNE, result.getType());

        verify(accountRepository, times(1)).save(account);
    }


    @Test
    void testCreateAccount_ClientNotFound() {
        when(accountMapper.toEntity(accountDTO)).thenReturn(account);
        String url = "http://localhost:8080/customer-service/customers/100";
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(Void.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        assertFalse(accountService.clientExists(100L));
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> accountService.createAccount(accountDTO));
        assertEquals("Le client avec ID 100 n'existe pas", exception.getMessage());

        verify(accountRepository, never()).save(any());
    }


    @Test
    void testCreateAccount_AlreadyExists() {
        when(accountMapper.toEntity(accountDTO)).thenReturn(account);
        String url = "http://localhost:8080/customer-service/customers/100";
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertTrue(accountService.clientExists(100L));
        when(accountRepository.existsByClientIdAndType(100L, TypeCompte.EPARGNE)).thenReturn(true);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> accountService.createAccount(accountDTO));
        assertEquals("Un client ne peut avoir qu'un seul compte de type EPARGNE", exception.getMessage());

        verify(accountRepository, never()).save(any());
    }

    @Test
    void testGetAccountById_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountService.getAccountById(1L);

        assertNotNull(result);
        assertEquals(TypeCompte.EPARGNE, result.getType());

        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAccountById_NotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(1L));

        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAccountsByClientId() {
        List<Account> accounts = List.of(account);
        List<AccountDTO> accountDTOs = List.of(accountDTO);

        when(accountRepository.findByClientId(100L)).thenReturn(accounts);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);

        List<AccountDTO> result = accountService.getAccountsByClientId(100L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(TypeCompte.EPARGNE, result.get(0).getType());

        verify(accountRepository, times(1)).findByClientId(100L);
    }

    @Test
    void testClientExists_Success() {
        String url = "http://localhost:8080/customer-service/customers/100";
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(Void.class)))
                .thenReturn(ResponseEntity.ok().build());

        assertTrue(accountService.clientExists(100L));
    }

    @Test
    void testClientExists_NotFound() {
        String url = "http://localhost:8080/customer-service/customers/100";
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(Void.class)))
                .thenThrow(HttpClientErrorException.NotFound.class);

        assertFalse(accountService.clientExists(100L));
    }

    @Test
    void testClientExists_ClientServiceException() {
        String url = "http://localhost:8080/customer-service/customers/100";
        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), isNull(), eq(Void.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        assertThrows(ClientServiceException.class, () -> accountService.clientExists(100L));
    }
}
