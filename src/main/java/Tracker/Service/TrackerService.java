package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import org.springframework.stereotype.Service;

import java.util.function.Function;


public interface TrackerService {
    TrackerRequestProjection getRequest(Torrent torrent);
    void computeUploaded(Torrent torrent, Function<Long, Long> compute);
    void computeDownloaded(Torrent torrent, Function<Long, Long> compute);
    void computeLeft(Torrent torrent, Function<Long, Long> compute);
    void notifySuccess(Torrent torrent);
    void notifyFailure(Torrent torrent);
}
