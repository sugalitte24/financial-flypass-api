package co.com.financial.api.adapters.in.web.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record CustomerRequest(
        @NotNull
        String identificationType,
        @NotBlank
        String identificationNumber,
        @NotBlank @Size(min = 3, max = 50)
        String firstName,
        String secondName,
        @NotBlank @Size(min = 3, max = 50)
        String lastName,
        @Email @NotBlank
        String email,
        @NotNull
        LocalDate birthDate
) {
}
