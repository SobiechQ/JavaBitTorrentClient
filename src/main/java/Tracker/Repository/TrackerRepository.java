package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Tracker;

import java.util.List;
import java.util.function.Function;

public interface TrackerRepository {
    List<Tracker> getFavorableTrackers(Torrent torrent);
    void addTrackers(Torrent torrent);
    void removeUnreachableTrackers(Torrent torrent);
    void notifyFailure(Tracker tracker);
}
