package co.com.financial.api.domain.model;

import co.com.financial.api.adapters.out.persistence.enums.TransactionType;
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
public class Transaction extends BaseModel {

    private UUID id;
    private TransactionType transactionType;
    private BigDecimal amount;
    private BigDecimal balanceAfter;
    private Account fromAccount;
    private Account toAccount;
    private String performedBy;

}
