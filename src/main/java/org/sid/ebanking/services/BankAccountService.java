package org.sid.ebanking.services;

import org.sid.ebanking.dtos.BankAccountDTO;
import org.sid.ebanking.dtos.CurrentBankAccountDTO;
import org.sid.ebanking.dtos.CustomerDTO;
import org.sid.ebanking.dtos.SavingBankAccountDTO;
import org.sid.ebanking.entities.BankAccount;

import org.sid.ebanking.exceptions.BalanceNotSufficientException;
import org.sid.ebanking.exceptions.BankAccountNotFoundException;
import org.sid.ebanking.exceptions.CustomersNotFoundException;

import java.util.List;

public interface BankAccountService {


    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraftLong  , Long customerId) throws CustomersNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double  interestRate  , Long customerId) throws CustomersNotFoundException;
    List<CustomerDTO> listCustomer();
    BankAccountDTO getBankAccount (String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount , String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount , String description) throws BalanceNotSufficientException, BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    List <BankAccountDTO> listBankAccounts();
    CustomerDTO getCustomer(Long customerId) throws CustomersNotFoundException;
    CustomerDTO updateCustomer(CustomerDTO customerDTO);
    void deleteCustomer(Long customerId);
}
