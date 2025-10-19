package ClientSession.Repository;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class ClientSessionRepositoryImpl implements ClientSessionRepository {
    private final Map<Torrent, ClientSessionRepositoryRecord> sessionRepository;
    private final Map<Torrent, ReentrantReadWriteLock> locks;

    public ClientSessionRepositoryImpl() {
        this.sessionRepository = new HashMap<>();
        this.locks = new ConcurrentHashMap<>();
    }

    @Override
    public boolean addSession(@NonNull Torrent torrent, @NonNull AsynchronousSocketChannel socket, @NonNull Peer peer) {
        final var lock = this.getLock(torrent).writeLock();
        try {
            lock.lock();
            return this.getClientSessionRepositoryRecord(torrent).tryAddSession(socket, peer);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeSession(@NonNull Torrent torrent,@NonNull Peer peer) {
        final var lock = this.getLock(torrent).writeLock();
        try {
            lock.lock();
            this.getClientSessionRepositoryRecord(torrent).removeSession(peer);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Set<Session> getSessions(@NonNull Torrent torrent) {
        final var lock = this.getLock(torrent).readLock();
        try {
            lock.lock();
            return this.getClientSessionRepositoryRecord(torrent).getSessions();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<Session> getSession(@NonNull Torrent torrent, @NonNull Peer peer) {
        final var lock = this.getLock(torrent).readLock();
        try {
            lock.lock();
            return this.getClientSessionRepositoryRecord(torrent).getSession(peer);
        } finally {
            lock.unlock();
        }
    }

    private ClientSessionRepositoryRecord getClientSessionRepositoryRecord(@NonNull Torrent torrent) {
        return this.sessionRepository.computeIfAbsent(torrent, _ -> new ClientSessionRepositoryRecord());
    }

    private synchronized ReentrantReadWriteLock getLock(@NonNull Torrent torrent) {
        return this.locks.computeIfAbsent(torrent, _ -> new ReentrantReadWriteLock());
    }
}
