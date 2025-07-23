package org.sid.ebanking.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebanking.dtos.CustomerDTO;
import org.sid.ebanking.entities.Customer;
import org.sid.ebanking.services.BankAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {

    private BankAccountService bankAccountService;


    @GetMapping("/customers")
    public List<CustomerDTO> customers() {
        return bankAccountService.listCustomer();
    }
}
