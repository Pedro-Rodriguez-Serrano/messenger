package dev.pedro.messenger.contacts.internal;

/** Error de negocio de contactos. El Reason decide el código HTTP. */
public class ContactException extends RuntimeException {

    public enum Reason { INVALID, FORBIDDEN, NOT_FOUND, CONFLICT }

    private final Reason reason;

    public ContactException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() { return reason; }
}