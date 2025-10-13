package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Messages.TrackerResponse;
import Tracker.Model.Tracker;
import Tracker.Repository.TrackerRepository;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Stream;


@Service
public class TrackerServiceImpl implements TrackerService {
    private final TrackerRepository trackerRepository;

    public TrackerServiceImpl(TrackerRepository trackerRepository) {
        this.trackerRepository = trackerRepository;
    }

    @Override
    public Stream<TrackerRequestProjection> getRequests(@NotNull Torrent torrent) {
        final var favorableTrackers = this.trackerRepository.getFavorableTrackers(torrent);
        return this.getRequests(torrent, favorableTrackers);
    }

    @Override
    public Stream<TrackerRequestProjection> getScheduledRequests(@NotNull Torrent torrent) {
        final var favorableTrackers = this.trackerRepository
                .getFavorableTrackers(torrent)
                .filter(Tracker::shouldAnnounce);
        return this.getRequests(torrent, favorableTrackers);
    }

    @Override
    public void removeUnreachableTrackers(@NonNull Torrent torrent) {
        this.trackerRepository.removeUnreachableTrackers(torrent);
    }

    private Stream<TrackerRequestProjection> getRequests(@NonNull Torrent torrent, Stream<Tracker> trackers) {
        final var uploaded = this.trackerRepository.getUploaded(torrent);
        final var downloaded = this.trackerRepository.getDownloaded(torrent);
        final var left = this.trackerRepository.getLeft(torrent);

        return trackers.map(t -> TrackerRequestProjection.builder()
                .tracker(t)
                .url(t.getUrl())
                .infoHashUrl(torrent.getInfoHashUrl())
                .uploaded(uploaded)
                .downloaded(downloaded)
                .left(left)
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
        this.trackerRepository.notifyFailure(tracker);
    }

    @Override
    public void computeUploaded(@NotNull Torrent torrent, Function<Long, Long> compute) {
        this.trackerRepository.computeUploaded(torrent, compute);
    }

    @Override
    public void computeDownloaded(@NotNull Torrent torrent, Function<Long, Long> compute) {
        this.trackerRepository.computeDownloaded(torrent, compute);
    }

    @Override
    public void computeLeft(@NotNull Torrent torrent, Function<Long, Long> compute) {
        this.trackerRepository.computeLeft(torrent, compute);
    }
}
