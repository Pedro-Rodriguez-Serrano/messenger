package dev.pedro.messenger.identity.internal;

/** Credenciales incorrectas. Se traduce a HTTP 401. */
public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Credenciales incorrectas");
    }
}