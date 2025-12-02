package co.com.financial.api.application.port.service;

import co.com.financial.api.adapters.in.web.controller.exceptions.AlreadyExistException;
import co.com.financial.api.adapters.in.web.controller.exceptions.ResourceNotFoundException;
import co.com.financial.api.adapters.in.web.mappers.CustomerMapper;
import co.com.financial.api.application.port.in.CustomerUseCase;
import co.com.financial.api.application.port.out.CustomerRepositoryPort;
import co.com.financial.api.domain.model.Customer;
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

    @Override
    public Customer createCustomer( Customer domain ) {
        repository.findByIdentificationTypeAndNumber(domain.getIdentificationType(), domain.getIdentificationNumber())
                .ifPresent(c -> {
                    throw new AlreadyExistException("Customer with same identification already exists");
                });
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
        repository.deleteById(id);
    }

}
