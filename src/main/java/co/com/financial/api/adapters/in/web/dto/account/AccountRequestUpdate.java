package co.com.financial.api.adapters.in.web.dto.account;

import java.util.UUID;

public record AccountRequestUpdate(
        UUID ownerId,
        String accountType,
        boolean exemptGmf
) {
}
