package dev.pedro.messenger.identity.internal;

import dev.pedro.messenger.identity.UserDirectory;
import dev.pedro.messenger.identity.UserSummary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserDirectoryService implements UserDirectory {

    private final UserRepository users;

    public UserDirectoryService(UserRepository users) {
        this.users = users;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserSummary> findByUsername(String username) {
        return users.findByUsername(username.trim().toLowerCase(Locale.ROOT))
                .map(UserDirectoryService::toSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<UUID, UserSummary> findAllByIds(Collection<UUID> ids) {
        return users.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getId, UserDirectoryService::toSummary));
    }

    private static UserSummary toSummary(User u) {
        return new UserSummary(u.getId(), u.getUsername(), u.getDisplayName(),
                u.getPersonalMessage(), u.getAvatarUrl());
    }
}