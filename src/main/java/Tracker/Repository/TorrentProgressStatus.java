package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Tracker;
import lombok.*;

@Data
@Getter
@Setter
public class TorrentProgressStatus {
    private long uploaded = 0;
    private long downloaded = 0;
    private long left = 0;
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private int currentLayer = 0;
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private int currentElement = 0;
    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private MultitrackerMetadataExtension multitrackerMetadataExtension;

    public TorrentProgressStatus(@NonNull Torrent torrent) {
        this.left = torrent.getLength();
        this.multitrackerMetadataExtension = new MultitrackerMetadataExtension(torrent);
    }

    public Tracker getTracker() {
        return multitrackerMetadataExtension.getTrackers(currentLayer)
                .skip(currentElement)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Progress status out of range"));

    }

    public void notifySuccess(@NonNull Tracker tracker) {
        this.multitrackerMetadataExtension.notifySuccess(tracker);
        currentElement = 0;
        currentLayer = 0;
    }

    public void reset() {
        currentElement = 0;
        currentLayer = 0;
    }

    public void next() {
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

    private int getLayersCount() {
        return multitrackerMetadataExtension.getLayersCount();
    }

    private int currentLayerSize() {
        return Math.toIntExact(multitrackerMetadataExtension.getTrackers(currentLayer).count());
    }


}
