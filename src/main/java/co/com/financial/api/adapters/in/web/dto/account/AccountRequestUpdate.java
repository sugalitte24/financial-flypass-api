package co.com.financial.api.adapters.in.web.dto.account;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AccountRequestUpdate(
        UUID ownerId,
        String accountType,
        boolean exemptGmf
) {}
