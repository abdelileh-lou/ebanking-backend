package org.sid.ebanking.services;

import org.sid.ebanking.dtos.CustomerDTO;
import org.sid.ebanking.entities.BankAccount;
import org.sid.ebanking.entities.CurrentAccount;
import org.sid.ebanking.entities.Customer;
import org.sid.ebanking.entities.SavingAccount;
import org.sid.ebanking.exceptions.BalanceNotSufficientException;
import org.sid.ebanking.exceptions.BankAccountNotFoundException;
import org.sid.ebanking.exceptions.CustomersNotFoundException;

import java.util.List;

public interface BankAccountService {
    Customer saveCustomer(Customer customer);
    CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraftLong  , Long customerId) throws CustomersNotFoundException;
    SavingAccount saveSavingBankAccount(double initialBalance, double  interestRate  , Long customerId) throws CustomersNotFoundException;
    List<CustomerDTO> listCustomer();
    BankAccount getBankAccount (String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount , String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount , String description) throws BalanceNotSufficientException, BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    List <BankAccount> listBankAccounts();
}
