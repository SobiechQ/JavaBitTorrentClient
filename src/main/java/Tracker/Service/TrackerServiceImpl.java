package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Tracker;
import Tracker.Repository.TorrentProgressStatus;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;


@Service
public class TrackerServiceImpl implements TrackerService {
    private final Map<Torrent, TorrentProgressStatus> progressStatusMap;
    private final Map<Torrent, ReentrantLock> locks;

    public TrackerServiceImpl() {
        this.progressStatusMap = new HashMap<>();
        this.locks = new ConcurrentHashMap<>();
    }

    @Override
    public TrackerRequestProjection getRequest(@NotNull Torrent torrent) {
        try {
            this.getLock(torrent).lock();
            final var status = this.getTorrentProgressStatus(torrent);
            final var projection = TrackerRequestProjection.builder()
                    .tracker(status.getTracker())
                    .url(status.getTracker().getUrl())
                    .infoHashUrl(torrent.getInfoHashUrl())
                    .uploaded(status.getUploaded())
                    .downloaded(status.getDownloaded())
                    .left(status.getLeft())
                    .build();
            status.next();
            return projection;
        } finally {
            this.getLock(torrent).unlock();
        }
    }


    @Override
    public void computeUploaded(@NotNull Torrent torrent, Function<Long, Long> compute) {
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
    public void computeDownloaded(@NotNull Torrent torrent, Function<Long, Long> compute) {
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
    public void computeLeft(@NotNull Torrent torrent, Function<Long, Long> compute) {
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

    @Override
    public void notifySuccess(@NonNull Torrent torrent, @NonNull Tracker tracker) {
        try {
            this.getLock(torrent).lock();
            this.getTorrentProgressStatus(torrent).notifySuccess(tracker);
        } finally {
            this.getLock(torrent).unlock();
        }
    }

    @Override
    public void reset(@NonNull Torrent torrent) {
        try {
            this.getLock(torrent).lock();
            this.getTorrentProgressStatus(torrent).reset();
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
