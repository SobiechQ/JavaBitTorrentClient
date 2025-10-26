package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Messages.TrackerResponse;
import Tracker.Model.Tracker;

import java.util.function.Function;
import java.util.stream.Stream;


public interface TrackerService {
    Stream<TrackerRequestProjection> getRequests(Torrent torrent);
    Stream<TrackerRequestProjection> getScheduledRequests(Torrent torrent);
    void handleResponse(Torrent torrent, TrackerResponse response);
    void removeUnreachableTrackers(Torrent torrent);
    void notifyFailure(Tracker tracker);
    void notifySuccess(TrackerResponse response);

}
