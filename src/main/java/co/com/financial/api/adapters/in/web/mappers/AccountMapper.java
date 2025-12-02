package co.com.financial.api.adapters.in.web.mappers;

import co.com.financial.api.adapters.in.web.dto.account.AccountRequest;
import co.com.financial.api.adapters.in.web.dto.account.AccountRequestUpdate;
import co.com.financial.api.adapters.in.web.dto.account.AccountResponse;
import co.com.financial.api.domain.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AccountMapper {

    AccountResponse toResponse( Account account );

    Account toDomain( AccountRequest accountRequest );

    Account toDomainUpdate( AccountRequestUpdate accountRequestUpdate );

    void updateAccount( Account account, @MappingTarget Account updatedAccount );

}
