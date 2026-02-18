package com.jpmc.midascore.model;

import com.jpmc.midascore.entity.UserRecord;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transaction_records")
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many transactions can belong to one sender
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private UserRecord sender;

    // Many transactions can belong to one recipient
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private UserRecord recipient;

    @Column(nullable = false)
    private BigDecimal amount;

    // ✅ NEW FIELD — incentive stored in DB
    @Column(nullable = false)
    private BigDecimal incentive;

    @Column(nullable = false)
    private Instant timestamp;

    // ---- Constructors ----

    public TransactionRecord() {
        // JPA needs this
    }

    // ✅ Updated constructor with incentive
    public TransactionRecord(UserRecord sender, UserRecord recipient, BigDecimal amount, BigDecimal incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.incentive = incentive;
        this.timestamp = Instant.now();
    }

    // ---- Getters ----

    public Long getId() { return id; }

    public UserRecord getSender() { return sender; }

    public UserRecord getRecipient() { return recipient; }

    public BigDecimal getAmount() { return amount; }

    public BigDecimal getIncentive() { return incentive; }

    public Instant getTimestamp() { return timestamp; }



    // ---- Setters ----

    public void setSender(UserRecord sender) { this.sender = sender; }

    public void setRecipient(UserRecord recipient) { this.recipient = recipient; }

    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public void setIncentive(BigDecimal incentive) { this.incentive = incentive; }
}
