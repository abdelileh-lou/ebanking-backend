package org.sid.ebanking.services;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.sid.ebanking.dtos.*;
import org.sid.ebanking.entities.*;
import org.sid.ebanking.enums.OperationType;
import org.sid.ebanking.exceptions.BalanceNotSufficientException;
import org.sid.ebanking.exceptions.BankAccountNotFoundException;
import org.sid.ebanking.exceptions.CustomersNotFoundException;
import org.sid.ebanking.mappers.BankAccountMapperImpl;
import org.sid.ebanking.repositories.AccountOperationRepository;
import org.sid.ebanking.repositories.BankAccountRepository;
import org.sid.ebanking.repositories.CustomerRepository;

import org.springframework.data.domain.PageRequest;
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
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving customer ");
        Customer customer = bankAccountMapperImpl.fromCustomerDTO(customerDTO);
        Customer saveCustomer =   customerRepository.save(customer);
        return bankAccountMapperImpl.fromCustomer(saveCustomer);

    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraftLong, Long customerId) throws CustomersNotFoundException {

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




        return bankAccountMapperImpl.fromCurrentBankAccount(saveBankAccount);
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomersNotFoundException {
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




        return bankAccountMapperImpl.fromSavingBankAccount(savingAccount);
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
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount =  bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("BankAccount not Found"));

        if(bankAccount instanceof SavingAccount){
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return bankAccountMapperImpl.fromSavingBankAccount(savingAccount);
        }else{

            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return bankAccountMapperImpl.fromCurrentBankAccount(currentAccount);

        }



    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount =  bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("BankAccount not Found"));

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
        BankAccount bankAccount =  bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("BankAccount not Found"));
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
    public List <BankAccountDTO> listBankAccounts(){
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOs = bankAccounts.stream().map(bankAccount -> {
            if (bankAccount instanceof SavingAccount) {
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return bankAccountMapperImpl.fromSavingBankAccount(savingAccount);
            } else {
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return bankAccountMapperImpl.fromCurrentBankAccount(currentAccount);
            }
        }).collect(Collectors.toList());


        return bankAccountDTOs;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomersNotFoundException {
         customerRepository.findById(customerId)
                                 .orElseThrow(() ->new CustomersNotFoundException("Customer Not Found"));
         return bankAccountMapperImpl.fromCustomer(customerRepository.findById(customerId).get());
    }


    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving customer ");
        Customer customer = bankAccountMapperImpl.fromCustomerDTO(customerDTO);
        Customer saveCustomer =   customerRepository.save(customer);
        return bankAccountMapperImpl.fromCustomer(saveCustomer);

    }

    @Override
    public void deleteCustomer(Long customerId)  {
        customerRepository.deleteById(customerId);

    }


    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
       List<AccountOperation> accountOperations=  accountOperationRepository.findByBankAccount_Id(accountId);
       List <AccountOperationDTO> accountOperationDTOS =  accountOperations.stream().map(accountOperation ->bankAccountMapperImpl.fromAccountOperation(accountOperation)).collect(Collectors.toList());

   return accountOperationDTOS;
    }


    @Override
    public AccountHistoryDTO getAccountHistory(String accountId , int page , int size) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if (bankAccount == null) throw  new BankAccountNotFoundException("Account Not Found");
       Page<AccountOperation>   accountOperationPage = accountOperationRepository.findByBankAccount_Id(accountId , PageRequest.of(page , size));
       AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
       List <AccountOperationDTO> accountOperationDTOS = accountOperationPage.getContent().stream().map(op -> bankAccountMapperImpl.fromAccountOperation(op)).collect(Collectors.toList());
       accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
       accountHistoryDTO.setAccountId(bankAccount.getId());
       accountHistoryDTO.setBalance(bankAccount.getBalance());
       accountHistoryDTO.setCurrentPage(page);
       accountHistoryDTO.setPageSize(size);
       accountHistoryDTO.setTotalPage(accountOperationPage.getTotalPages());
       return  accountHistoryDTO;
    }
}
