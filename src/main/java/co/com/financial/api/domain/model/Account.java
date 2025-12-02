package co.com.financial.api.domain.model;


import co.com.financial.api.domain.enums.AccountStatus;
import co.com.financial.api.domain.enums.AccountType;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account extends BaseModel {

    private UUID id;
    private AccountType accountType;
    private String accountNumber;
    private AccountStatus status;
    private BigDecimal balance;
    private BigDecimal availableBalance;
    private boolean exemptGmf;
    private Customer owner;
}

