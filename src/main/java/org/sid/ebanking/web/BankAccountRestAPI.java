package org.sid.ebanking.web;

import lombok.AllArgsConstructor;
import org.sid.ebanking.dtos.AccountHistoryDTO;
import org.sid.ebanking.dtos.AccountOperationDTO;
import org.sid.ebanking.dtos.BankAccountDTO;
import org.sid.ebanking.exceptions.BankAccountNotFoundException;
import org.sid.ebanking.services.BankAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
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


}
