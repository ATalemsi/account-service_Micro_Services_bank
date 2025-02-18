package org.cibanc.account_service.controller;


import lombok.AllArgsConstructor;
import org.cibanc.account_service.dto.AccountDTO;
import org.cibanc.account_service.model.Account;
import org.cibanc.account_service.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@AllArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO accountDTO) {
        AccountDTO createdAccount = accountService.createAccount(accountDTO);
        return ResponseEntity.ok(createdAccount);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        AccountDTO accountDTO = accountService.getAccountById(id);
        return ResponseEntity.ok(accountDTO);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AccountDTO>> getAccountsByClientId(@PathVariable Long clientId) {
        List<AccountDTO> accounts = accountService.getAccountsByClientId(clientId);
        return ResponseEntity.ok(accounts);
    }
}
