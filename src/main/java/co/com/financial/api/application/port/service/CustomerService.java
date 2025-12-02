package co.com.financial.api.application.port.service;

import co.com.financial.api.adapters.in.web.controller.exceptions.AlreadyExistException;
import co.com.financial.api.adapters.in.web.controller.exceptions.GenericException;
import co.com.financial.api.adapters.in.web.controller.exceptions.ResourceNotFoundException;
import co.com.financial.api.adapters.in.web.controller.exceptions.UnderAgeException;
import co.com.financial.api.adapters.in.web.mappers.CustomerMapper;
import co.com.financial.api.adapters.out.persistence.repository.AccountRepository;
import co.com.financial.api.application.port.in.CustomerUseCase;
import co.com.financial.api.application.port.out.CustomerRepositoryPort;
import co.com.financial.api.domain.model.Customer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService implements CustomerUseCase {

    private final CustomerRepositoryPort repository;
    private final CustomerMapper mapper;
    private final AccountRepository accountRepo;

    @Override
    public Customer createCustomer( Customer domain ) {
        repository.findByIdentificationTypeAndNumber(domain.getIdentificationType(), domain.getIdentificationNumber())
                .ifPresent(c -> {
                    throw new AlreadyExistException("Customer with same identification already exists");
                });
        validarMayorDeEdad(domain.getBirthDate());
        return repository.save(domain);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById( UUID id ) {
        return repository.findById(id).orElse(new Customer());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    @Override
    public Customer updateCustomer( UUID id, Customer domain ) {
        Customer existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));
        mapper.updateCustomer(domain, existing);
        return repository.save(existing);
    }

    @Override
    public void deleteCustomer( UUID id ) {
        var accounts = accountRepo.findByOwner_Id(id);
        if (!accounts.isEmpty()) throw new GenericException("Customer owns products and cannot be removed.");
        repository.deleteById(id);
    }

    public void validarMayorDeEdad( LocalDate fechaNacimiento ) {
        LocalDate hoy = LocalDate.now();

        int edad = Period.between(fechaNacimiento, hoy).getYears();

        if (edad < 18) {
            throw new UnderAgeException("Must be of legal age. Current age: " + edad);
        }
    }
}
