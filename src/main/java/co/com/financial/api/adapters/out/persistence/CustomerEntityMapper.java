package co.com.financial.api.adapters.out.persistence;

import co.com.financial.api.adapters.out.persistence.entities.CustomerEntity;
import co.com.financial.api.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerEntityMapper {

    @Mapping(target = "accounts", ignore = true)
    CustomerEntity toCustomerEntity( Customer domain );

    @Mapping(target = "accounts", ignore = true)
    Customer toDomain( CustomerEntity customerEntity );

    void update( Customer customer, @MappingTarget CustomerEntity customerEntity );
}
