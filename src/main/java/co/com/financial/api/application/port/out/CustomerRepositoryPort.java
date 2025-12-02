package co.com.financial.api.application.port.out;

import co.com.financial.api.adapters.out.persistence.enums.IdentificationType;
import co.com.financial.api.domain.model.Customer;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepositoryPort {

    Customer save( Customer customer );

    Optional<Customer> findById( UUID id );

    List<Customer> findAll();

    void deleteById( UUID id );

    Optional<Customer> findByIdentificationTypeAndNumber( IdentificationType identificationType, String identificationNumber );
}
