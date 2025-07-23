package org.sid.ebanking;

import org.sid.ebanking.entities.*;
import org.sid.ebanking.enums.AccountStatus;
import org.sid.ebanking.enums.OperationType;
import org.sid.ebanking.exceptions.BalanceNotSufficientException;
import org.sid.ebanking.exceptions.BankAccountNotFoundException;
import org.sid.ebanking.exceptions.CustomersNotFoundException;
import org.sid.ebanking.repositories.AccountOperationRepository;
import org.sid.ebanking.repositories.BankAccountRepository;
import org.sid.ebanking.repositories.CustomerRepository;
import org.sid.ebanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingApplication {

	private final CustomerRepository customerRepository;

	public EbankingApplication(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(EbankingApplication.class, args);
	}


	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
		return args -> {
			Stream.of("Hassan" , "Imane"  , "Mohamed").forEach(name ->{
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customer);
			});

			bankAccountService.listCustomer().forEach(customer -> {
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000 , 9000 ,customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*12000 , 5.5 ,customer.getId());
//					bankAccountService.listBankAccounts().forEach(account -> {
//					for (int i = 0 ; i <10 ; i++){
//						bankAccountService.credit(account.getId() , 10000+Math.random()*12000 , "Credit");
//					}});

					List<BankAccount> bankAccounts = bankAccountService.listBankAccounts();
					for (BankAccount bankAccount : bankAccounts) {
						for (int i =0 ; i<10 ; i++){
							bankAccountService.credit(bankAccount.getId() , 10000+Math.random()*120000, "Credit");
							bankAccountService.debit(bankAccount.getId() , 1000+Math.random()*9000, "Debit");
						}
					}
				}catch (CustomersNotFoundException | BalanceNotSufficientException | BankAccountNotFoundException e){
					e.printStackTrace();
				}
			});
		};
	}

//	@Bean
	CommandLineRunner start(CustomerRepository customerRepository, BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository) {
		return args ->{
			Stream.of("Hassan" , "Yassine" , "Aicha").forEach(name ->{
				Customer customer = new Customer();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				customerRepository.save(customer);
			});

			customerRepository.findAll().forEach(cust ->{
				CurrentAccount currentAccount = new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random() * 9000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);

				SavingAccount savingAccount = new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random() * 9000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);
			});

			bankAccountRepository.findAll().forEach(acc ->{
				for (int i = 0 ;  i < 5 ; i++){
					AccountOperation accountOperation = new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random() * 12000);
					accountOperation.setType(Math.random()>0.5? OperationType.DEBIT : OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}
			});

		};
	}



}
