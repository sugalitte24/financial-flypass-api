package co.com.financial.api.adapters.in.web.mappers;

import co.com.financial.api.adapters.in.web.dto.client.CustomerRequest;
import co.com.financial.api.adapters.in.web.dto.client.CustomerRequestUpdate;
import co.com.financial.api.adapters.in.web.dto.client.CustomerResponse;
import co.com.financial.api.domain.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface CustomerMapper {

    CustomerResponse customerDomainToResponse( Customer customer );

    Customer customerRequestToDomain( CustomerRequest customerRequest );

    Customer customerRequestToDomain( CustomerRequestUpdate requestUpdate );

    void updateCustomer( Customer customerOrigin, @MappingTarget Customer customer );
}
