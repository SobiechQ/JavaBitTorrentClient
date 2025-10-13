package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Tracker;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
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
    public Stream<Tracker> getFavorableTrackers(@NonNull Torrent torrent) {
        try {
            this.getLock(torrent).lock();
            return this.getTorrentProgressStatus(torrent)
                    .getFavorableTrackers();
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public void removeUnreachableTrackers(@NonNull Torrent torrent) {
        try {
            this.getLock(torrent).lock();
            this.getFavorableTrackers(torrent)
                    .filter(Tracker::isUnreachable)
                    .toList()
                    .forEach(t -> this.getTorrentProgressStatus(torrent).removeTracker(t));
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public long getUploaded(@NonNull Torrent torrent) {
        try {
            this.getLock(torrent).lock();
            return this.getTorrentProgressStatus(torrent).getUploaded();
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public long getDownloaded(@NonNull Torrent torrent) {
        try {
            this.getLock(torrent).lock();
            return this.getTorrentProgressStatus(torrent).getDownloaded();
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public long getLeft(@NonNull Torrent torrent) {
        try {
            this.getLock(torrent).lock();
            return this.getTorrentProgressStatus(torrent).getLeft();
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public void notifyFailure(@NonNull Tracker tracker) {
        final var torrent = tracker.getTorrent();
        try {
            this.getLock(torrent).lock();
            this.getTorrentProgressStatus(torrent).notifyFailure(tracker);
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public void computeUploaded(@NonNull Torrent torrent, Function<Long, Long> compute) {
        try {
            this.getLock(torrent).lock();
            final var progres = this.getTorrentProgressStatus(torrent);
            final var computed = compute.apply(progres.getUploaded());
            if (computed <= 0)
                throw new IllegalStateException("Computed uploaded value cant be negative");
            progres.setUploaded(computed);
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public void computeDownloaded(@NonNull Torrent torrent, Function<Long, Long> compute) {
        try {
            this.getLock(torrent).lock();
            final var progres = this.getTorrentProgressStatus(torrent);
            final var computed = compute.apply(progres.getDownloaded());
            if (computed <= 0)
                throw new IllegalStateException("Computed downloaded value cant be negative");
            progres.setDownloaded(computed);
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public void computeLeft(@NonNull Torrent torrent, Function<Long, Long> compute) {
        try {
            this.getLock(torrent).lock();
            final var progres = this.getTorrentProgressStatus(torrent);
            final var computed = compute.apply(progres.getLeft());
            if (computed <= 0)
                throw new IllegalStateException("Computed left value cant be negative");
            progres.setLeft(computed);
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    private TorrentProgressStatus getTorrentProgressStatus(@NonNull Torrent torrent) {
        return this.progressStatusMap.computeIfAbsent(torrent, TorrentProgressStatus::new);
    }

    private synchronized ReentrantLock getLock(@NonNull Torrent torrent) {
        return this.locks.computeIfAbsent(torrent, _ -> new ReentrantLock());
    }
}
