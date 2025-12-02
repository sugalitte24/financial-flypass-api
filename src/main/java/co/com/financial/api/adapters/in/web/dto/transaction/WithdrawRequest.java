package co.com.financial.api.adapters.in.web.dto.transaction;

public record WithdrawRequest(String fromAccountNumber, java.math.BigDecimal amount, String performedBy) {
}