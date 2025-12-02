package co.com.financial.api.adapters.in.web.mappers;

import co.com.financial.api.adapters.in.web.dto.client.CustomerRequestUpdate;
import co.com.financial.api.adapters.in.web.dto.transaction.TransactionResponse;
import co.com.financial.api.adapters.in.web.dto.transaction.TransferRequest;
import co.com.financial.api.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TransactionMapper {

    TransactionResponse transactionDomainToResponse( Transaction transaction );

    Transaction transactionRequestToDomain( TransferRequest customerRequest );

    Transaction transactionRequestToDomain( CustomerRequestUpdate requestUpdate );
}
