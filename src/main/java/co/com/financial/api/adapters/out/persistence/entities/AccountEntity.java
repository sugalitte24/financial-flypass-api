package co.com.financial.api.adapters.out.persistence.entities;

import co.com.financial.api.adapters.out.persistence.enums.AccountStatus;
import co.com.financial.api.adapters.out.persistence.enums.AccountType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "accounts",
        uniqueConstraints = @UniqueConstraint(name = "uk_account_number", columnNames = "account_number"),
        indexes = {@Index(name = "idx_account_owner", columnList = "owner_id")})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountEntity extends BaseModel {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType;

    @NotNull
    @Column(name = "account_number", length = 10, nullable = false, unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private AccountStatus status;

    @NotNull
    @Column(name = "balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal balance;

    @NotNull
    @Column(name = "available_balance", precision = 19, scale = 2, nullable = false)
    private BigDecimal availableBalance;

    @Column(name = "exempt_gmf", nullable = false)
    private boolean exemptGmf;
    ;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, foreignKey = @ForeignKey(name = "fk_account_owner"))
    private CustomerEntity owner;


    @PrePersist
    protected void prePersist() {
        if (this.id == null) this.id = UUID.randomUUID();
        setCreatedAt(LocalDateTime.now());

        if (this.accountType == AccountType.SAVINGS && this.status == null) {
            this.status = AccountStatus.ACTIVE;
        }

        if (this.balance == null) this.balance = BigDecimal.ZERO;
        if (this.availableBalance == null) this.availableBalance = this.balance;

        if (this.accountType == AccountType.SAVINGS && this.balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Savings account cannot have negative balance");
        }
    }

    @PreUpdate
    protected void preUpdate() {
        setUpdatedAt(LocalDateTime.now());

        if (this.accountType == AccountType.SAVINGS && this.balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Savings account cannot have negative balance");
        }

        if (this.status == AccountStatus.CANCELED && this.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Only accounts with zero balance can be cancelled");
        }
    }

    public void credit( BigDecimal amount ) {
        if (amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");
        this.balance = this.balance.add(amount);
        this.availableBalance = this.availableBalance.add(amount);
    }

    public void debit( BigDecimal amount ) {
        if (amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive");

        if (this.accountType == AccountType.SAVINGS && this.balance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds for savings account");
        }
        if (this.balance.subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        this.balance = this.balance.subtract(amount);
        this.availableBalance = this.availableBalance.subtract(amount);
    }

}

