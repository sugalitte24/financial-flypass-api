package co.com.financial.api.adapters.out.persistence.adapter;

import co.com.financial.api.adapters.out.persistence.AccountEntityMapper;
import co.com.financial.api.adapters.out.persistence.entities.AccountEntity;
import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.adapters.out.persistence.repository.AccountRepository;
import co.com.financial.api.application.port.out.AccountRepositoryPort;
import co.com.financial.api.domain.model.Account;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccountAdapterJpa implements AccountRepositoryPort {
    private final AccountRepository jpa;
    private final AccountEntityMapper mapper;

    @Override
    public Account save( Account account ) {
        AccountEntity entity = mapper.toCustomerEntity(account);
        AccountEntity saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById( UUID id ) {
        return jpa.findById(id).map(mapper::toDomain);
    }


    @Override
    public List<Account> getAccountsByCustomer( UUID customerId ) {
        return jpa.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void changeStatus( UUID id, AccountStatus status, UUID performedBy ) {
        jpa.updateStatus(id, status, performedBy);
    }

    @Override
    public Optional<Account> findByIdAndOwner( UUID id, UUID performedBy ) {
        return jpa.findByIdAndOwner_Id(id, performedBy).map(mapper::toDomain);
    }

    @Override
    public void cancelAccount( UUID id, UUID performedBy ) {
        jpa.updateStatus(id, AccountStatus.CANCELED, performedBy);
    }
}
