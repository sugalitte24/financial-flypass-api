package co.com.financial.api.adapters.in.web.dto.transaction;

public record TransferRequest(String fromAccountNumber, String toAccountNumber, java.math.BigDecimal amount,
                              String performedBy) {
}