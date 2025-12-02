package co.com.financial.api.adapters.in.web.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransferResponse(
        UUID transactionId,
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        LocalDateTime date,
        BigDecimal fromBalanceAfter,
        BigDecimal toBalanceAfter
) {
}