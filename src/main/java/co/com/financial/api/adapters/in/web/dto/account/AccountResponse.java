package co.com.financial.api.adapters.in.web.dto.account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record AccountResponse(UUID id, String accountType, String accountNumber, String status,
                              BigDecimal balance, BigDecimal availableBalance,
                              boolean exemptGmf, UUID ownerId,
                              LocalDateTime createdAt, OffsetDateTime updatedAt) {
}