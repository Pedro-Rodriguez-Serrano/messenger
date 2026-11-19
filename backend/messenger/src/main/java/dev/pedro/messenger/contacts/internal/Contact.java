package dev.pedro.messenger.contacts.internal;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Una relación entre dos usuarios: pendiente o aceptada. Se corresponde con la tabla "contacts".
 */
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

    /**
     * Este método comprueba si la solicitud ha sido aceptada.
     *
     *  @return un booleano:
     * - true: si el estado es "accepted".
     * - false: si el estado es cualquier otro valor.
     *
     */
    boolean isAccepted() {
        /*ACCEPTED.equals(status) en lugar de status.equals(ACCEPTED)?
        Para evitar un posible NullPointerException si status fuese nulo.*/
        return ACCEPTED.equals(status);
    }

    /**
     * Este método permite aceptar una solicitud de contacto.
     *
     * Realiza dos operaciones:
     * 1. Cambia el estado de pending a accepted.
     * 2. Registra el momento exacto en el que se acepta.
     */
    void accept() {
        this.status = ACCEPTED;
        this.respondedAt = Instant.now();
    }

    /**
     * Este método comprueba si un usuario participa en la relación.
     *
     * @return true si:
     * - El usuario es quien envió la solicitud.
     * - El usuario es quien recibió la solicitud.
     * @param user
     *
     */
    boolean involves(UUID user) {
        return requesterId.equals(user) || addresseeId.equals(user);
    }

    /**
     * Este método devuelve el identificador del otro usuario que participa en la relación.
     *
     * @return :
     * Si el usuario es el solicitante, devuelve el destinatario.
     * Si no, devuelve el solicitante.
     */
    UUID otherThan(UUID user) {
        return requesterId.equals(user) ? addresseeId : requesterId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public UUID getAddresseeId() {
        return addresseeId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }
}