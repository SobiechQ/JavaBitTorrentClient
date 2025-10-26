package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Peer.Repository.PeerRepository;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Messages.TrackerResponse;
import Tracker.Model.Tracker;
import Tracker.Repository.TrackerRepository;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Function;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class TrackerServiceImpl implements TrackerService {
    private static final Logger log = LoggerFactory.getLogger(TrackerServiceImpl.class);
    private final TrackerRepository trackerRepository;
    private final PeerRepository peerRepository;
    private final TrackerStatusRetrieverService trackerStatusRetrieverService;

    @Override
    public Stream<TrackerRequestProjection> getRequests(@NotNull Torrent torrent) {
        trackerRepository.addTrackers(torrent);
        final var favorableTrackers = this.trackerRepository.getFavorableTrackers(torrent).stream();
        return this.getRequests(torrent, favorableTrackers);
    }

    @Override
    public Stream<TrackerRequestProjection> getScheduledRequests(@NotNull Torrent torrent) {
        final var favorableTrackers = this.trackerRepository
                .getFavorableTrackers(torrent)
                .stream()
                .filter(Tracker::shouldAnnounce);
        return this.getRequests(torrent, favorableTrackers);
    }

    @Override
    public void handleResponse(@NotNull Torrent torrent, @NonNull TrackerResponse response) {
        log.info("Handling tracker response, peer count [{}]", response.getPeers().count());
        response.getPeers()
                .forEach(p -> peerRepository.addPeer(torrent, p));

    }

    @Override
    public void removeUnreachableTrackers(@NonNull Torrent torrent) {
        this.trackerRepository.removeUnreachableTrackers(torrent);
    }

    private Stream<TrackerRequestProjection> getRequests(@NonNull Torrent torrent, Stream<Tracker> trackers) {
        final var uploaded = trackerStatusRetrieverService.getUploaded(torrent);
        final var downloaded = trackerStatusRetrieverService.getDownloaded(torrent);
        final var left = trackerStatusRetrieverService.getLeft(torrent);

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
}
