package dev.pedro.messenger.contacts.internal;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/** Una relación entre dos usuarios: pendiente o aceptada. Se corresponde con la tabla "contacts". */
@Entity
@Table(name = "contacts")
public class Contact {

    static final String PENDING = "pending";
    static final String ACCEPTED = "accepted";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "requester_id", nullable = false, updatable = false)
    private UUID requesterId;

    @Column(name = "addressee_id", nullable = false, updatable = false)
    private UUID addresseeId;

    @Column(nullable = false, length = 10)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    protected Contact() {
        // Constructor vacío que exige JPA
    }

    Contact(UUID requesterId, UUID addresseeId) {
        this.requesterId = requesterId;
        this.addresseeId = addresseeId;
        this.status = PENDING;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    boolean isAccepted() { return ACCEPTED.equals(status); }

    void accept() {
        this.status = ACCEPTED;
        this.respondedAt = Instant.now();
    }

    boolean involves(UUID user) {
        return requesterId.equals(user) || addresseeId.equals(user);
    }

    /** Devuelve el otro extremo de la relación. */
    UUID otherThan(UUID user) {
        return requesterId.equals(user) ? addresseeId : requesterId;
    }

    public UUID getId() { return id; }
    public UUID getRequesterId() { return requesterId; }
    public UUID getAddresseeId() { return addresseeId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getRespondedAt() { return respondedAt; }
}