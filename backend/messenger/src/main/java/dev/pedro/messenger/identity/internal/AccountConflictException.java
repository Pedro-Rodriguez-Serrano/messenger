package dev.pedro.messenger.identity.internal;

/** Se lanza cuando el username o el email ya están en uso. Se traduce a HTTP 409. */
public class AccountConflictException extends RuntimeException {
    public AccountConflictException(String message) {
        super(message);
    }
}