package org.sid.ebanking.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sid.ebanking.dtos.CustomerDTO;
import org.sid.ebanking.entities.Customer;
import org.sid.ebanking.exceptions.CustomersNotFoundException;
import org.sid.ebanking.mappers.BankAccountMapperImpl;
import org.sid.ebanking.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {

    private BankAccountService bankAccountService;

    private BankAccountMapperImpl bankAccountMapperImpl;


    @GetMapping("/customers")
    public List<CustomerDTO> customers() {
        return bankAccountService.listCustomer();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id") Long customerId) throws CustomersNotFoundException {
        return bankAccountService.getCustomer(customerId);

    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO) throws CustomersNotFoundException {
      return bankAccountService.saveCustomer(customerDTO);
    }

    @PutMapping("/customers/{customerId}")
    public CustomerDTO updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO) throws CustomersNotFoundException {

        customerDTO.setId(customerId);
        return bankAccountService.updateCustomer(customerDTO);

    }

    @DeleteMapping("/customers/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId) throws CustomersNotFoundException {
         bankAccountService.deleteCustomer(customerId);
    }

}
