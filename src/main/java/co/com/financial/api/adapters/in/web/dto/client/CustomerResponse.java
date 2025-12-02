package co.com.financial.api.adapters.in.web.dto.client;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerResponse(UUID id, String identificationType, String identificationNumber,
                               String firstName, String secondName, String lastName,
                               String email, LocalDate birthDate) {
}