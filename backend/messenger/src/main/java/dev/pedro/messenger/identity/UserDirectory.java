package dev.pedro.messenger.identity;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** API pública del módulo identity para el resto de módulos. */
public interface UserDirectory {

    /** Busca por username (sin distinguir mayúsculas). Vacío si no existe. */
    Optional<UserSummary> findByUsername(String username);

    /** Devuelve los usuarios que existan entre esos ids. Los inexistentes simplemente no aparecen. */
    Map<UUID, UserSummary> findAllByIds(Collection<UUID> ids);
}