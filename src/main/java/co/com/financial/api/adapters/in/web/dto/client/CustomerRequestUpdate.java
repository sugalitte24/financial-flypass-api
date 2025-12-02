package co.com.financial.api.adapters.in.web.dto.client;

import java.time.LocalDate;

public record CustomerRequestUpdate(
        String identificationType,
        String identificationNumber,
        String firstName,
        String secondName,
        String lastName,
        String email,
        LocalDate birthDate
) {
}
