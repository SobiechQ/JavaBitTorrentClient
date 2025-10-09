package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Tracker;
import Tracker.Repository.MultitrackerMetadataExtension;
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
    @Data
    private final static class TorrentProgressStatus {
        @Getter @Setter private long uploaded = 0;
        @Getter @Setter private long downloaded = 0;
        @Getter @Setter private long left = 0;
        private int currentLayer = 0;
        private int currentElement = 0;
        private MultitrackerMetadataExtension multitrackerMetadataExtension;

        private TorrentProgressStatus(@NonNull Torrent torrent) {
            this.left = torrent.getLength();
            this.multitrackerMetadataExtension = new MultitrackerMetadataExtension(torrent);
        }

        private Tracker getTracker() {
            return multitrackerMetadataExtension.getTrackers(currentLayer)
                    .skip(currentElement)
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Progress status out of range"));

        }

        private void next() {
            if (currentElement < currentLayerSize() - 1){
                currentElement++;
                return;
            }
            currentElement = 0;
            if (currentLayer < getLayersCount() - 1) {
                currentLayer++;
                return;
            }
            currentLayer = 0;
        }

        private void notifySuccess(@NonNull Tracker tracker) {
            this.multitrackerMetadataExtension.notifySuccess(tracker);
            currentElement = 0;
            currentLayer = 0;
        }

        private int getLayersCount() {
            return multitrackerMetadataExtension.getLayersCount();
        }

        private int currentLayerSize() {
            return Math.toIntExact(multitrackerMetadataExtension.getTrackers(currentLayer).count());
        }

        private void reset() {
            currentElement = 0;
            currentLayer = 0;
        }
    }

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
                    .uploaded(status.uploaded)
                    .downloaded(status.downloaded)
                    .left(status.left)
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
