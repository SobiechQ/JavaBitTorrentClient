package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Tracker;
import lombok.*;

import java.util.stream.Stream;

@Data
@Getter
@Setter
class TorrentProgressStatus {
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private MultitrackerMetadataExtension multitrackerMetadataExtension;

    TorrentProgressStatus(@NonNull Torrent torrent) {
        this.multitrackerMetadataExtension = new MultitrackerMetadataExtension(torrent);
    }

    void notifyFailure(@NonNull Tracker tracker) {
        this.multitrackerMetadataExtension.notifyFailure(tracker);
    }

    Stream<Tracker> getFavorableTrackers() {
        return this.multitrackerMetadataExtension.getFavorableTrackers();
    }

    void removeTracker(@NonNull Tracker tracker) {
        this.multitrackerMetadataExtension.removeTracker(tracker);
    }
}
