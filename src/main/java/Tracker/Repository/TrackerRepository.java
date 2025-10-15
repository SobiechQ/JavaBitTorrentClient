package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Tracker;

import java.util.List;
import java.util.function.Function;

public interface TrackerRepository {
    List<Tracker> getFavorableTrackers(Torrent torrent);
    void removeUnreachableTrackers(Torrent torrent);
    long getUploaded(Torrent torrent);
    long getDownloaded(Torrent torrent);
    long getLeft(Torrent torrent);
    void notifyFailure(Tracker tracker);
    void computeUploaded(Torrent torrent, Function<Long, Long> compute);
    void computeDownloaded(Torrent torrent, Function<Long, Long> compute);
    void computeLeft(Torrent torrent, Function<Long, Long> compute);
}
