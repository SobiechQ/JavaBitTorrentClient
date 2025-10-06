package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Repository.MultitrackerMetadataExtension;
import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class TrackerServiceImpl implements TrackerService {
    @Data
    private final static class TorrentProgressStatus {
        private long uploaded = 0;
        private long downloaded = 0;
        private long left = 0;
        private MultitrackerMetadataExtension multitrackerMetadataExtension;

        private TorrentProgressStatus(@NonNull Torrent torrent, @NonNull MultitrackerMetadataExtension multitrackerMetadataExtension) {
            this.left = torrent.getLength();
            this.multitrackerMetadataExtension = multitrackerMetadataExtension;
        }
    }

    private final Map<Torrent, TorrentProgressStatus> progressStatusMap;


    public TrackerServiceImpl() {
        this.progressStatusMap = new HashMap<>();
    }

    @Override
    public TrackerRequestProjection getRequest(@NotNull Torrent torrent) {
        final var status = this.getTorrentProgressStatus(torrent);

//        return new TrackerRequestProjection()
        return null;
    }

    //zarzadzanie jaki url

    @Override
    public void computeUploaded(@NotNull Torrent torrent, Function<Long, Long> compute) {


    }

    @Override
    public void computeDownloaded(@NotNull Torrent torrent, Function<Long, Long> compute) {

    }

    @Override
    public void computeLeft(@NotNull Torrent torrent, Function<Long, Long> compute) {

    }

    private TorrentProgressStatus getTorrentProgressStatus(@NonNull Torrent torrent) {
//        return this.progressStatusMap.computeIfAbsent(torrent, TorrentProgressStatus::new);
        return null;
    }


}
