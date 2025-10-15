package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Tracker;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Stream;

@Repository
public class TrackerRepositoryImpl implements TrackerRepository {
    private final Map<Torrent, TorrentProgressStatus> progressStatusMap;
    private final Map<Torrent, ReentrantLock> locks;

    public TrackerRepositoryImpl() {
        this.progressStatusMap = new HashMap<>();
        this.locks = new ConcurrentHashMap<>();
    }

    @Override
    public List<Tracker> getFavorableTrackers(@NonNull Torrent torrent) {
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            return this.getTorrentProgressStatus(torrent)
                    .getFavorableTrackers()
                    .toList();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void removeUnreachableTrackers(@NonNull Torrent torrent) {
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            this.getFavorableTrackers(torrent)
                    .stream()
                    .filter(Tracker::isUnreachable)
                    .toList()
                    .forEach(t -> this.getTorrentProgressStatus(torrent).removeTracker(t));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getUploaded(@NonNull Torrent torrent) {
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            return this.getTorrentProgressStatus(torrent).getUploaded();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getDownloaded(@NonNull Torrent torrent) {
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            return this.getTorrentProgressStatus(torrent).getDownloaded();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public long getLeft(@NonNull Torrent torrent) {
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            return this.getTorrentProgressStatus(torrent).getLeft();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void notifyFailure(@NonNull Tracker tracker) {
        final var torrent = tracker.getTorrent();
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            this.getTorrentProgressStatus(torrent).notifyFailure(tracker);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void computeUploaded(@NonNull Torrent torrent, Function<Long, Long> compute) {
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            final var progres = this.getTorrentProgressStatus(torrent);
            final var computed = compute.apply(progres.getUploaded());
            if (computed <= 0)
                throw new IllegalStateException("Computed uploaded value cant be negative");
            progres.setUploaded(computed);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void computeDownloaded(@NonNull Torrent torrent, Function<Long, Long> compute) {
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            final var progres = this.getTorrentProgressStatus(torrent);
            final var computed = compute.apply(progres.getDownloaded());
            if (computed <= 0)
                throw new IllegalStateException("Computed downloaded value cant be negative");
            progres.setDownloaded(computed);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void computeLeft(@NonNull Torrent torrent, Function<Long, Long> compute) {
        final var lock = this.getLock(torrent);
        try {
            lock.lock();
            final var progres = this.getTorrentProgressStatus(torrent);
            final var computed = compute.apply(progres.getLeft());
            if (computed <= 0)
                throw new IllegalStateException("Computed left value cant be negative");
            progres.setLeft(computed);
        } finally {
            lock.unlock();
        }
    }

    private TorrentProgressStatus getTorrentProgressStatus(@NonNull Torrent torrent) {
        return this.progressStatusMap.computeIfAbsent(torrent, TorrentProgressStatus::new);
    }

    private synchronized ReentrantLock getLock(@NonNull Torrent torrent) {
        return this.locks.computeIfAbsent(torrent, _ -> new ReentrantLock());
    }
}
