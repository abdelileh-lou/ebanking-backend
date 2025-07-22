package org.sid.ebanking.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("CA")
@Entity
public class CurrentAccount extends  BankAccount {

    private double overDraft;

}
