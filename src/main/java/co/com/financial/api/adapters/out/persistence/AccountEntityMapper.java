package co.com.financial.api.adapters.out.persistence;

import co.com.financial.api.adapters.out.persistence.entities.AccountEntity;
import co.com.financial.api.adapters.out.persistence.entities.CustomerEntity;
import co.com.financial.api.domain.model.Account;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountEntityMapper {

    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "ownerIdToEntity")
    AccountEntity toAccountEntity( Account domain );

    @Mapping(source = "owner.id", target = "ownerId")
    Account toDomain( AccountEntity accountEntity );

    @Mapping(target = "owner", ignore = true)
    void update( Account account, @MappingTarget AccountEntity accountEntity );

    @Named("ownerIdToEntity")
    default CustomerEntity ownerIdToEntity( UUID ownerId ) {
        if (ownerId == null) return null;
        CustomerEntity e = new CustomerEntity();
        e.setId(ownerId);
        return e;
    }
}
