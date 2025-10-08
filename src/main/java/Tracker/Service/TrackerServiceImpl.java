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

        private void notifyFailure() {
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

        private void notifySuccess() {
            this.multitrackerMetadataExtension.notifySuccess(getTracker());
            currentElement = 0;
        }

        private int getLayersCount() {
            return multitrackerMetadataExtension.getLayersCount();
        }

        private int currentLayerSize() {
            return Math.toIntExact(multitrackerMetadataExtension.getTrackers(currentLayer).count());
        }
    }

    private final Map<Torrent, TorrentProgressStatus> progressStatusMap;

    public TrackerServiceImpl() {
        this.progressStatusMap = new HashMap<>();
    }

    @Override
    public TrackerRequestProjection getRequest(@NotNull Torrent torrent) {
        final var status = this.getTorrentProgressStatus(torrent);
        return TrackerRequestProjection.builder()
                .url(status.getTracker().getUrl())
                .infoHashUrl(torrent.getInfoHashUrl())
                .uploaded(status.uploaded)
                .downloaded(status.downloaded)
                .left(status.left)
                .build();
    }


    @Override
    public void computeUploaded(@NotNull Torrent torrent, Function<Long, Long> compute) {
        final var progres = this.getTorrentProgressStatus(torrent);
        final var computed = compute.apply(progres.getUploaded());
        if (computed <= 0)
            throw new IllegalStateException("Computed uploaded value cant be negative");
        progres.setUploaded(computed);
    }

    @Override
    public void computeDownloaded(@NotNull Torrent torrent, Function<Long, Long> compute) {
        final var progres = this.getTorrentProgressStatus(torrent);
        final var computed = compute.apply(progres.getDownloaded());
        if (computed <= 0)
            throw new IllegalStateException("Computed downloaded value cant be negative");
        progres.setDownloaded(computed);
    }

    @Override
    public void computeLeft(@NotNull Torrent torrent, Function<Long, Long> compute) {
        final var progres = this.getTorrentProgressStatus(torrent);
        final var computed = compute.apply(progres.getLeft());
        if (computed <= 0)
            throw new IllegalStateException("Computed left value cant be negative");
        progres.setLeft(computed);
    }

    @Override
    public void notifySuccess(@NonNull Torrent torrent) {
        this.getTorrentProgressStatus(torrent).notifySuccess();
    }

    @Override
    public void notifyFailure(@NonNull Torrent torrent) {
        this.getTorrentProgressStatus(torrent).notifyFailure();
    }

    private TorrentProgressStatus getTorrentProgressStatus(@NonNull Torrent torrent) {
        return this.progressStatusMap.computeIfAbsent(torrent, TorrentProgressStatus::new);
    }
}
