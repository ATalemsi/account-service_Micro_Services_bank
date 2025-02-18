package org.cibanc.account_service.model;

import lombok.Data;
import org.cibanc.account_service.model.enums.TypeCompte;

@Data
public class Account {
    private Long id;
    private double solde;
    private TypeCompte type; // COURANT / EPARGNE
    private Long clientId;
}
