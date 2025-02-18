package org.cibanc.account_service.dto;

import lombok.Data;
import org.cibanc.account_service.model.enums.TypeCompte;

@Data
public class AccountDTO {
    private Long id;
    private double solde;
    private TypeCompte type;
}