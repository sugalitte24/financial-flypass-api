package co.com.financial.api.adapters.out.persistence.repository;

import co.com.financial.api.adapters.out.persistence.entities.CustomerEntity;
import co.com.financial.api.adapters.out.persistence.enums.IdentificationType;
import jakarta.validation.constraints.NotBlank;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID> {
    Optional<CustomerEntity> findByIdentificationTypeAndIdentificationNumber( IdentificationType identificationType, @NotBlank String identificationNumber );
}
