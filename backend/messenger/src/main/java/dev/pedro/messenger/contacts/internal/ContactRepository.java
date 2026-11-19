package dev.pedro.messenger.contacts.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

    /** La relación entre dos usuarios, da igual quién la inició. */
    @Query("select c from Contact c where (c.requesterId = :a and c.addresseeId = :b) "
            + "or (c.requesterId = :b and c.addresseeId = :a)")
    Optional<Contact> findBetween(@Param("a") UUID a, @Param("b") UUID b);

    @Query("select c from Contact c where c.status = 'accepted' "
            + "and (c.requesterId = :userId or c.addresseeId = :userId) order by c.respondedAt desc")
    List<Contact> findFriendships(@Param("userId") UUID userId);

    List<Contact> findByAddresseeIdAndStatusOrderByCreatedAtDesc(UUID addresseeId, String status);

    List<Contact> findByRequesterIdAndStatusOrderByCreatedAtDesc(UUID requesterId, String status);
}