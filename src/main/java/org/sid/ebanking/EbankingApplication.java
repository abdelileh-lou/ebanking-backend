package org.sid.ebanking;

import org.sid.ebanking.entities.*;
import org.sid.ebanking.enums.AccountStatus;
import org.sid.ebanking.enums.OperationType;
import org.sid.ebanking.repositories.AccountOperationRepository;
import org.sid.ebanking.repositories.BankAccountRepository;
import org.sid.ebanking.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
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
	CommandLineRunner commandLineRunner(BankAccountRepository bankAccountRepository, AccountOperationRepository accountOperationRepository) {
		return args -> {
			BankAccount bankAccount = bankAccountRepository.findById("351f1e29-e8a7-4681-a733-4d2452f760e4").orElse(null);
			if (bankAccount != null) {
				System.out.println("Bank account not found");
				System.out.println(bankAccount.getId());
				System.out.println(bankAccount.getBalance());
				System.out.println(bankAccount.getStatus());
				System.out.println(bankAccount.getCreatedAt());
				System.out.println(bankAccount.getCustomer().getName());
				System.out.println(bankAccount.getClass().getSimpleName());

				if (bankAccount instanceof CurrentAccount){
					System.out.println("Over Draft=>"+((CurrentAccount)bankAccount).getOverDraft());
				}else if (bankAccount instanceof SavingAccount){
					System.out.println("Rate=>"+((SavingAccount)bankAccount).getInterestRate());

				}
				bankAccount.getOperations().forEach(op ->{
					System.out.println(op.getType() + "\t" + op.getOperationDate() + "\t" + op.getAmount());
				});
			}
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
