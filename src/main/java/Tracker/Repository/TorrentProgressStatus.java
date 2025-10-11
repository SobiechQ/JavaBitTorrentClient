package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Tracker;
import lombok.*;

import java.util.stream.Stream;

@Data
@Getter
@Setter
public class TorrentProgressStatus {
    private long uploaded = 0;
    private long downloaded = 0;
    private long left = 0;

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private MultitrackerMetadataExtension multitrackerMetadataExtension;

    public TorrentProgressStatus(@NonNull Torrent torrent) {
        this.left = torrent.getLength();
        this.multitrackerMetadataExtension = new MultitrackerMetadataExtension(torrent);
    }

    public void notifyFailure(@NonNull Tracker tracker) {
        this.multitrackerMetadataExtension.notifyFailure(tracker);
    }

    public Stream<Tracker> getFavorableTrackers() {
        return this.multitrackerMetadataExtension.getFavorableTrackers();
    }
}
