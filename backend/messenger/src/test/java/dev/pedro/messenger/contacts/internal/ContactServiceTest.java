package dev.pedro.messenger.contacts.internal;

import dev.pedro.messenger.contacts.internal.ContactException.Reason;
import dev.pedro.messenger.identity.UserDirectory;
import dev.pedro.messenger.identity.UserSummary;
import org.junit.jupiter.api.Test;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ContactServiceTest {

    private final ContactRepository repo = mock(ContactRepository.class);
    private final UserDirectory users = mock(UserDirectory.class);
    private final ContactService service = new ContactService(repo, users);

    private final UUID ana = UUID.randomUUID();
    private final UUID pepe = UUID.randomUUID();
    private final UUID luis = UUID.randomUUID();

    private UserSummary summary(UUID id, String username) {
        return new UserSummary(id, username, username, null, null);
    }

    private void assertReason(Runnable action, Reason expected) {
        assertThatThrownBy(action::run)
                .isInstanceOfSatisfying(ContactException.class,
                        e -> assertThat(e.reason()).isEqualTo(expected));
    }

    @Test
    void cannotSendRequestToYourself() {
        when(users.findByUsername("ana")).thenReturn(Optional.of(summary(ana, "ana")));
        assertReason(() -> service.sendRequest(ana, "ana"), Reason.INVALID);
    }

    @Test
    void cannotSendRequestToUnknownUser() {
        when(users.findByUsername("fantasma")).thenReturn(Optional.empty());
        assertReason(() -> service.sendRequest(ana, "fantasma"), Reason.NOT_FOUND);
    }

    @Test
    void cannotDuplicateRequestInEitherDirection() {
        when(users.findByUsername("pepe")).thenReturn(Optional.of(summary(pepe, "pepe")));
        // Ya existe una solicitud iniciada por Pepe hacia Ana
        when(repo.findBetween(ana, pepe)).thenReturn(Optional.of(new Contact(pepe, ana)));

        assertReason(() -> service.sendRequest(ana, "pepe"), Reason.CONFLICT);
        verify(repo, never()).saveAndFlush(any());
    }

    @Test
    void sendsPendingRequest() {
        when(users.findByUsername("pepe")).thenReturn(Optional.of(summary(pepe, "pepe")));
        when(repo.findBetween(ana, pepe)).thenReturn(Optional.empty());
        when(repo.saveAndFlush(any(Contact.class))).thenAnswer(i -> i.getArgument(0));

        var response = service.sendRequest(ana, "pepe");

        assertThat(response.user().username()).isEqualTo("pepe");
        verify(repo).saveAndFlush(any(Contact.class));
    }

    @Test
    void onlyTheAddresseeCanAccept() {
        UUID requestId = UUID.randomUUID();
        when(repo.findById(requestId)).thenReturn(Optional.of(new Contact(ana, pepe)));

        assertReason(() -> service.accept(ana, requestId), Reason.FORBIDDEN);  // quien la envió
        assertReason(() -> service.accept(luis, requestId), Reason.FORBIDDEN); // un tercero
    }

    @Test
    void addresseeAcceptsRequest() {
        UUID requestId = UUID.randomUUID();
        Contact contact = new Contact(ana, pepe);
        when(repo.findById(requestId)).thenReturn(Optional.of(contact));
        when(users.findAllByIds(any())).thenReturn(Map.of(ana, summary(ana, "ana")));

        var response = service.accept(pepe, requestId);

        assertThat(contact.isAccepted()).isTrue();
        assertThat(response.user().username()).isEqualTo("ana");
    }

    @Test
    void thirdPartyCannotCancelARequest() {
        UUID requestId = UUID.randomUUID();
        when(repo.findById(requestId)).thenReturn(Optional.of(new Contact(ana, pepe)));

        assertReason(() -> service.deleteRequest(luis, requestId), Reason.FORBIDDEN);
        verify(repo, never()).delete(any());
    }

    @Test
    void cannotRemoveSomeoneWhoIsOnlyPending() {
        when(repo.findBetween(ana, pepe)).thenReturn(Optional.of(new Contact(ana, pepe)));
        assertReason(() -> service.removeContact(ana, pepe), Reason.NOT_FOUND);
    }
}