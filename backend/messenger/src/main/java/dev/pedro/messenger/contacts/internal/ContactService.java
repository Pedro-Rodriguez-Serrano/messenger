package dev.pedro.messenger.contacts.internal;

import dev.pedro.messenger.contacts.internal.ContactException.Reason;
import dev.pedro.messenger.identity.UserDirectory;
import dev.pedro.messenger.identity.UserSummary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContactService {

    private final ContactRepository contacts;
    private final UserDirectory users;

    public ContactService(ContactRepository contacts, UserDirectory users) {
        this.contacts = contacts;
        this.users = users;
    }

    /** Mis amigos (solicitudes aceptadas), los más recientes primero. */
    @Transactional(readOnly = true)
    public List<ContactResponse> listFriends(UUID me) {
        List<Contact> friendships = contacts.findFriendships(me);
        Map<UUID, UserSummary> profiles = profilesOf(friendships, me);
        return friendships.stream()
                .filter(c -> profiles.containsKey(c.otherThan(me)))
                .map(c -> new ContactResponse(profiles.get(c.otherThan(me)), c.getRespondedAt()))
                .toList();
    }

    /** Solicitudes pendientes. direction: "incoming" (recibidas) o "outgoing" (enviadas). */
    @Transactional(readOnly = true)
    public List<ContactRequestResponse> listRequests(UUID me, String direction) {
        boolean incoming = switch (direction) {
            case "incoming" -> true;
            case "outgoing" -> false;
            default -> throw new ContactException(Reason.INVALID,
                    "direction debe ser 'incoming' o 'outgoing'");
        };
        List<Contact> pending = incoming
                ? contacts.findByAddresseeIdAndStatusOrderByCreatedAtDesc(me, Contact.PENDING)
                : contacts.findByRequesterIdAndStatusOrderByCreatedAtDesc(me, Contact.PENDING);
        Map<UUID, UserSummary> profiles = profilesOf(pending, me);
        return pending.stream()
                .filter(c -> profiles.containsKey(c.otherThan(me)))
                .map(c -> new ContactRequestResponse(c.getId(), profiles.get(c.otherThan(me)), c.getCreatedAt()))
                .toList();
    }

    /**
     * Envía una solicitud de amistad por username.
     * @throws ContactException NOT_FOUND si no existe el usuario, INVALID si eres tú,
     *         CONFLICT si ya sois amigos o ya hay una solicitud (en cualquier dirección)
     */
    @Transactional
    public ContactRequestResponse sendRequest(UUID me, String username) {
        UserSummary target = users.findByUsername(username)
                .orElseThrow(() -> new ContactException(Reason.NOT_FOUND, "No existe ese usuario"));
        if (target.id().equals(me)) {
            throw new ContactException(Reason.INVALID, "No puedes añadirte a ti mismo");
        }
        contacts.findBetween(me, target.id()).ifPresent(existing -> {
            throw new ContactException(Reason.CONFLICT, existing.isAccepted()
                    ? "Ya sois amigos"
                    : "Ya hay una solicitud pendiente entre vosotros");
        });
        try {
            Contact saved = contacts.saveAndFlush(new Contact(me, target.id()));
            return new ContactRequestResponse(saved.getId(), target, saved.getCreatedAt());
        } catch (DataIntegrityViolationException e) {
            // Dos solicitudes simultáneas: el índice único de la BDD actúa de árbitro
            throw new ContactException(Reason.CONFLICT, "Ya hay una solicitud pendiente entre vosotros");
        }
    }

    /**
     * Acepta una solicitud. Solo puede hacerlo su destinatario. Aceptar dos veces es inofensivo.
     * @throws ContactException NOT_FOUND si no existe, FORBIDDEN si no va dirigida a ti
     */
    @Transactional
    public ContactResponse accept(UUID me, UUID requestId) {
        Contact contact = contacts.findById(requestId)
                .orElseThrow(() -> new ContactException(Reason.NOT_FOUND, "La solicitud no existe"));
        if (!contact.getAddresseeId().equals(me)) {
            throw new ContactException(Reason.FORBIDDEN, "La solicitud no va dirigida a ti");
        }
        if (!contact.isAccepted()) {
            contact.accept();
        }
        UserSummary requester = users.findAllByIds(List.of(contact.getRequesterId()))
                .get(contact.getRequesterId());
        if (requester == null) {
            throw new ContactException(Reason.NOT_FOUND, "El usuario ya no existe");
        }
        return new ContactResponse(requester, contact.getRespondedAt());
    }

    /**
     * Rechaza una solicitud recibida o cancela una enviada.
     * @throws ContactException NOT_FOUND si no existe o ya fue aceptada, FORBIDDEN si no participas
     */
    @Transactional
    public void deleteRequest(UUID me, UUID requestId) {
        Contact contact = contacts.findById(requestId)
                .orElseThrow(() -> new ContactException(Reason.NOT_FOUND, "La solicitud no existe"));
        if (!contact.involves(me)) {
            throw new ContactException(Reason.FORBIDDEN, "No participas en esa solicitud");
        }
        if (contact.isAccepted()) {
            throw new ContactException(Reason.NOT_FOUND, "Esa solicitud ya fue aceptada; elimina al amigo");
        }
        contacts.delete(contact);
    }

    /** Elimina a un amigo. @throws ContactException NOT_FOUND si no sois amigos */
    @Transactional
    public void removeContact(UUID me, UUID otherUserId) {
        Contact contact = contacts.findBetween(me, otherUserId)
                .filter(Contact::isAccepted)
                .orElseThrow(() -> new ContactException(Reason.NOT_FOUND, "No sois amigos"));
        contacts.delete(contact);
    }

    private Map<UUID, UserSummary> profilesOf(List<Contact> list, UUID me) {
        return users.findAllByIds(list.stream().map(c -> c.otherThan(me)).toList());
    }
}