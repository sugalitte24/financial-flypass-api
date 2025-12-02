package co.com.financial.api.adapters.in.web.dto.transaction;

import java.math.BigDecimal;

public record DepositRequest(String toAccountNumber, BigDecimal amount, String performedBy) {
}
