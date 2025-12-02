package co.com.financial.api.adapters.out.persistence.entities;


import co.com.financial.api.adapters.out.persistence.enums.IdentificationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customers", indexes = {
        @Index(name = "idx_customer_ident", columnList = "identification_type, identification_number", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEntity extends BaseModel {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @NotBlank
    @Column(name = "identification_number", length = 50, nullable = false)
    private String identificationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", length = 20, nullable = false)
    private IdentificationType identificationType;


    @NotBlank
    @Column(name = "first_name", length = 120, nullable = false)
    private String firstName;

    @Column(name = "second_name", length = 120)
    private String secondName;

    @NotBlank
    @Column(name = "last_name", length = 120, nullable = false)
    private String lastName;

    @Email
    @NotBlank
    @Column(name = "email", length = 200, nullable = false)
    private String email;

    @Past
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountEntity> accounts = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID();
        setCreatedAt(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdate() {
        setUpdatedAt(LocalDateTime.now());
    }

    public void addAccount( AccountEntity account ) {
        accounts.add(account);
        account.setOwner(this);
    }

    public void removeAccount( AccountEntity account ) {
        accounts.remove(account);
        account.setOwner(null);
    }

}
