/**
 * Módulo de contactos: amistades y solicitudes.
 * Solo puede depender de la API pública de identity.
 */
@org.springframework.modulith.ApplicationModule(allowedDependencies = "identity")
package dev.pedro.messenger.contacts;