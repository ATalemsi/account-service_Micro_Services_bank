package org.cibanc.account_service.mapper;

import org.cibanc.account_service.dto.AccountDTO;
import org.cibanc.account_service.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountDTO toDTO(Account account);
    Account toEntity(AccountDTO accountDTO);
}

