package dev.pedro.messenger.contacts.internal;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/contacts")
public class ContactsController {

    private final ContactService service;

    public ContactsController(ContactService service) {
        this.service = service;
    }

    /** GET /api/v1/contacts: mis amigos. */
    @GetMapping
    public List<ContactResponse> list(@AuthenticationPrincipal Jwt jwt) {
        return service.listFriends(me(jwt));
    }

    /** GET /api/v1/contacts/requests?direction=incoming|outgoing */
    @GetMapping("/requests")
    public List<ContactRequestResponse> requests(@AuthenticationPrincipal Jwt jwt,
                                                 @RequestParam String direction) {
        return service.listRequests(me(jwt), direction);
    }

    /** POST /api/v1/contacts/requests: 201, 400, 404 o 409. */
    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactRequestResponse send(@AuthenticationPrincipal Jwt jwt,
                                       @Valid @RequestBody SendContactRequest body) {
        return service.sendRequest(me(jwt), body.username());
    }

    /** POST /api/v1/contacts/requests/{id}/accept: 200, 403 o 404. */
    @PostMapping("/requests/{requestId}/accept")
    public ContactResponse accept(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId) {
        return service.accept(me(jwt), requestId);
    }

    /** DELETE /api/v1/contacts/requests/{id}: rechazar o cancelar. 204, 403 o 404. */
    @DeleteMapping("/requests/{requestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRequest(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID requestId) {
        service.deleteRequest(me(jwt), requestId);
    }

    /** DELETE /api/v1/contacts/{userId}: eliminar amigo. 204 o 404. */
    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID userId) {
        service.removeContact(me(jwt), userId);
    }

    /** El id del usuario autenticado viaja en el "subject" del token. */
    private static UUID me(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}