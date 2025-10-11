package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Messages.TrackerResponse;
import Tracker.Model.Tracker;
import Tracker.Repository.TorrentProgressStatus;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Stream;


@Service
public class TrackerServiceImpl implements TrackerService {
    private final Map<Torrent, TorrentProgressStatus> progressStatusMap;
    private final Map<Torrent, ReentrantLock> locks;


    public TrackerServiceImpl() {
        this.progressStatusMap = new HashMap<>();
        this.locks = new ConcurrentHashMap<>();
    }

    @Override
    public Stream<TrackerRequestProjection> getRequests(@NotNull Torrent torrent) {
        final var favorableTrackers = this.getTorrentProgressStatus(torrent).getFavorableTrackers();
        return this.getRequests(torrent, favorableTrackers);
    }

    @Override
    public Stream<TrackerRequestProjection> getScheduledRequests(@NotNull Torrent torrent) {
        final var favorableTrackers = this.getTorrentProgressStatus(torrent)
                .getFavorableTrackers()
                .filter(Tracker::shouldAnnounce);
        return this.getRequests(torrent, favorableTrackers);
    }

    private Stream<TrackerRequestProjection> getRequests(@NonNull Torrent torrent, Stream<Tracker> trackers) {
        final var status = this.getTorrentProgressStatus(torrent);

        return trackers.map(t -> TrackerRequestProjection.builder()
                .tracker(t)
                .url(t.getUrl())
                .infoHashUrl(torrent.getInfoHashUrl())
                .uploaded(status.getUploaded())
                .downloaded(status.getDownloaded())
                .left(status.getLeft())
                .build());
    }

    @Override
    public void notifySuccess(@NotNull TrackerResponse response) {
        final var tracker = response.getRespondTo();
        tracker.updateLastSeen();
        tracker.setInterval(response.getInterval());
    }

    @Override
    public void notifyFailure(@NotNull Tracker tracker) {
        this.getTorrentProgressStatus(tracker.getTorrent()).notifyFailure(tracker);
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

    private TorrentProgressStatus getTorrentProgressStatus(@NonNull Torrent torrent) {
        return this.progressStatusMap.computeIfAbsent(torrent, TorrentProgressStatus::new);
    }

    private synchronized ReentrantLock getLock(@NonNull Torrent torrent) {
        return this.locks.computeIfAbsent(torrent, _ -> new ReentrantLock());
    }
}
