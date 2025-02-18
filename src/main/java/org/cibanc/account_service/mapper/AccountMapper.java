package org.cibanc.account_service.mapper;

import org.cibanc.account_service.dto.AccountDTO;
import org.cibanc.account_service.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {
    AccountMapper INSTANCE = Mappers.getMapper(AccountMapper.class);

    AccountDTO toDTO(Account account);
    Account toEntity(AccountDTO accountDTO);
}
