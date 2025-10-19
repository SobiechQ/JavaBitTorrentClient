package ClientSession.Repository;

import Peer.Model.Peer;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

class ClientSessionRepositoryRecord {
    private static final Logger log = LoggerFactory.getLogger(ClientSessionRepositoryRecord.class);
    private final ConcurrentHashMap<Peer, Session> sessions;
    private final Semaphore semaphore;

    ClientSessionRepositoryRecord() {
        this.sessions = new ConcurrentHashMap<>();
        this.semaphore = new Semaphore(50);
    }

    boolean tryAddSession(@NonNull AsynchronousSocketChannel socket, @NonNull Peer peer) {
        if (!this.semaphore.tryAcquire()) {
            log.debug("Connection limit reached, skipping {}", peer);
            return false;
        }
        final var session = new Session(socket, peer);
        this.sessions.put(peer, session);
        return true;
    }

    void removeSession(@NonNull Peer peer) {
        Optional.ofNullable(this.sessions.remove(peer))
                .ifPresent(session -> {
                    session.close();
                    this.semaphore.release();
                });
    }

    Set<Session> getSessions() {
        return this.sessions
                .values()
                .stream()
                .collect(Collectors.toUnmodifiableSet());
    }

    Optional<Session> getSession(@NonNull Peer peer) {
        return Optional.ofNullable(this.sessions.get(peer));
    }
}
