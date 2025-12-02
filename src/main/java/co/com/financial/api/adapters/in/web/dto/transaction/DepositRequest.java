package co.com.financial.api.adapters.in.web.dto.transaction;

public record DepositRequest(String toAccountNumber, java.math.BigDecimal amount, String performedBy) {
}
