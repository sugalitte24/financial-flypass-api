package co.com.financial.api.domain.model;

import co.com.financial.api.adapters.out.persistence.enums.IdentificationType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseModel {

    private UUID id;
    private String identificationNumber;
    private IdentificationType identificationType;
    private String firstName;
    private String secondName;
    private String lastName;
    private String email;
    private LocalDate birthDate;
    private List<Account> accounts = new ArrayList<>();
}
