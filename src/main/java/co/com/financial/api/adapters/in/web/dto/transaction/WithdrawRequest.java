package co.com.financial.api.adapters.in.web.dto.transaction;

import java.math.BigDecimal;

public record WithdrawRequest(String fromAccountNumber, BigDecimal amount, String performedBy) {
}