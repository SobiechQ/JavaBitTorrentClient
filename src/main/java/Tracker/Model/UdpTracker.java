package Tracker.Model;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Messages.TrackerResponse;
import io.vavr.NotImplementedError;
import lombok.NonNull;

import java.net.URI;

public class UdpTracker extends Tracker{
    public UdpTracker(@NonNull URI uri, @NonNull Torrent torrent) {
        super(uri, torrent);
//        throw new NotImplementedError("Udp trackers are not yet supported");
    }

    @Override
    public TrackerResponse announce(TrackerRequestProjection requestProjection) {
        throw new NotImplementedError("Udp trackers are not yet supported");
    }
}
