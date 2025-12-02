package co.com.financial.api.adapters.out.persistence;

import co.com.financial.api.adapters.out.persistence.entities.TransactionEntity;
import co.com.financial.api.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TransactionEntityMapper {

    TransactionEntity toTransactionEntity( Transaction domain );

    Transaction toDomain( TransactionEntity customerEntity );

}
