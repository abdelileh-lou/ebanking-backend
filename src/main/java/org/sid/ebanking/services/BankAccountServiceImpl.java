package org.sid.ebanking.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebanking.dtos.CustomerDTO;
import org.sid.ebanking.entities.*;
import org.sid.ebanking.enums.OperationType;
import org.sid.ebanking.exceptions.BalanceNotSufficientException;
import org.sid.ebanking.exceptions.BankAccountNotFoundException;
import org.sid.ebanking.exceptions.CustomersNotFoundException;
import org.sid.ebanking.mappers.BankAccountMapperImpl;
import org.sid.ebanking.repositories.AccountOperationRepository;
import org.sid.ebanking.repositories.BankAccountRepository;
import org.sid.ebanking.repositories.CustomerRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImpl implements BankAccountService {


    private CustomerRepository customerRepository;

    private BankAccountMapperImpl bankAccountMapperImpl;

    private BankAccountRepository bankAccountRepository;

    private AccountOperationRepository accountOperationRepository;




    @Override
    public Customer saveCustomer(Customer customer) {
        log.info("Saving customer ");
        Customer saveCustomer =   customerRepository.save(customer);
        return saveCustomer;

    }

    @Override
    public CurrentAccount saveCurrentBankAccount(double initialBalance, double overDraftLong, Long customerId) throws CustomersNotFoundException {

        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new CustomersNotFoundException("Customer not found");


        CurrentAccount currentAccount = new CurrentAccount() ;
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreatedAt(new Date());
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraftLong);

        CurrentAccount saveBankAccount = bankAccountRepository.save(currentAccount);




        return saveBankAccount;
    }

    @Override
    public SavingAccount saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomersNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null)
            throw new CustomersNotFoundException("Customer not found");


        SavingAccount savingAccount = new SavingAccount() ;
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);

        SavingAccount saveBankAccount = bankAccountRepository.save(savingAccount);




        return savingAccount;
    }


    @Override
    public List<CustomerDTO> listCustomer() {
        List <Customer> customers = customerRepository.findAll();
      List <CustomerDTO> customerDTOS = customers.stream()
                                            .map(customer -> bankAccountMapperImpl.fromCustomer(customer))
                                            .collect(Collectors.toList());

      return customerDTOS;
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount =  bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("BankAccount not Found"));
        return  bankAccount;


    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = getBankAccount(accountId);
        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");

        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance() - amount);

        bankAccountRepository.save(bankAccount);


    }

    @Override
    public void credit(String accountId, double amount, String description) throws  BankAccountNotFoundException {
        BankAccount bankAccount = getBankAccount(accountId);
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);

        bankAccount.setBalance(bankAccount.getBalance() + amount);

        bankAccountRepository.save(bankAccount);

    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        debit(accountIdSource , amount , "Transfer to"+accountIdDestination);
        credit(accountIdDestination, amount, "Transfer from"+accountIdSource);

    }

    @Override
    public List <BankAccount> listBankAccounts(){
        return bankAccountRepository.findAll();
    }
}
