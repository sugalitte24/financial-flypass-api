package co.com.financial.api.adapters.out.persistence.entities;

import co.com.financial.api.adapters.out.persistence.enums.TransactionType;
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
import jakarta.persistence.Table;
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
@Table(name = "transactions", indexes = {
        @Index(name = "idx_transaction_from", columnList = "from_account_id"),
        @Index(name = "idx_transaction_to", columnList = "to_account_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity extends BaseModel {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", length = 20, nullable = false)
    private TransactionType transactionType;

    @NotNull
    @Column(name = "amount", precision = 19, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "balance_after", precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", foreignKey = @ForeignKey(name = "fk_tx_from_account"))
    private AccountEntity fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", foreignKey = @ForeignKey(name = "fk_tx_to_account"))
    private AccountEntity toAccount;

    @Column(name = "performed_by", length = 120)
    private String performedBy;


    @PrePersist
    protected void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID();
        setCreatedAt(LocalDateTime.now());

        if (this.amount == null || this.amount.signum() <= 0) {
            throw new IllegalStateException("Transaction amount must be positive");
        }

        switch (this.transactionType) {
            case DEPOSIT -> {
                if (this.toAccount == null) throw new IllegalStateException("Deposit requires toAccount");
            }
            case WITHDRAWAL -> {
                if (this.fromAccount == null) throw new IllegalStateException("Withdrawal requires fromAccount");
            }
            case TRANSFER -> {
                if (this.fromAccount == null || this.toAccount == null)
                    throw new IllegalStateException("Transfer requires both fromAccount and toAccount");
                if (this.fromAccount.equals(this.toAccount))
                    throw new IllegalStateException("Transfer between same account is not allowed");
            }
        }
    }
}
