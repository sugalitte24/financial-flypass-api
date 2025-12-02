package co.com.financial.api.adapters.out.persistence.adapter;

import co.com.financial.api.adapters.out.persistence.CustomerEntityMapper;
import co.com.financial.api.adapters.out.persistence.entities.CustomerEntity;
import co.com.financial.api.adapters.out.persistence.enums.IdentificationType;
import co.com.financial.api.adapters.out.persistence.repository.CustomerRepository;
import co.com.financial.api.application.port.out.CustomerRepositoryPort;
import co.com.financial.api.domain.model.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerAdapterJpa implements CustomerRepositoryPort {

    private final CustomerRepository jpa;
    private final CustomerEntityMapper mapper;

    @Override
    public Customer save( Customer customer ) {
        CustomerEntity entity = mapper.toCustomerEntity(customer);
        CustomerEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Customer> findById( UUID id ) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Customer> findByIdentificationTypeAndNumber( IdentificationType identificationType, String identificationNumber ) {
        return jpa.findByIdentificationTypeAndIdentificationNumber(identificationType, identificationNumber)
                .map(mapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById( UUID id ) {
        jpa.deleteById(id);
    }
}