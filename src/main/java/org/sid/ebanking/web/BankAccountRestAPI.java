package org.sid.ebanking.web;

import lombok.AllArgsConstructor;
import org.sid.ebanking.dtos.*;
import org.sid.ebanking.exceptions.BalanceNotSufficientException;
import org.sid.ebanking.exceptions.BankAccountNotFoundException;
import org.sid.ebanking.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    @GetMapping("/accounts/{accountId}")
    public BankAccountDTO getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return  bankAccountService.getBankAccount(accountId);
    }



    @GetMapping("/accounts")
    public List<BankAccountDTO> listBankAccounts() throws BankAccountNotFoundException {
        return bankAccountService.listBankAccounts();
    }

    @GetMapping("/accounts/{accountId}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable String accountId) throws BankAccountNotFoundException  {

        return bankAccountService.accountHistory(accountId);

    }

    @GetMapping("accounts/{accountId}/pageOperations")
    public AccountHistoryDTO getHistoryPage(
            @PathVariable String accountId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "4") int size) throws BankAccountNotFoundException {

       return bankAccountService.getAccountHistory(accountId , page ,size);
    }


    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO) throws BalanceNotSufficientException, BankAccountNotFoundException {
        this.bankAccountService.debit(debitDTO.getAccountId() , debitDTO.getAmount() , debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BalanceNotSufficientException, BankAccountNotFoundException {
        bankAccountService.credit(creditDTO.getAccountId() , creditDTO.getAmount() , creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/transfer")
    public void transfer(@RequestBody TransferDTO transferDTO) throws BankAccountNotFoundException, BalanceNotSufficientException {
        bankAccountService.transfer(transferDTO.getAccountSource(), transferDTO.getAccountDestination() , transferDTO.getAmount() , transferDTO.getDescription());

    }


}
