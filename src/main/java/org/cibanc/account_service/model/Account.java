package org.cibanc.account_service.model;

import jakarta.persistence.*;
import lombok.*;
import org.cibanc.account_service.model.enums.TypeCompte;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clientId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TypeCompte type;

    private Double solde;
}
