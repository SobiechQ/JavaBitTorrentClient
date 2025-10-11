package Tracker.Controller;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Tracker.Model.Messages.TrackerResponse;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public interface TrackerController {
    CompletableFuture<TrackerResponse> asyncAnnounce(Torrent torrent);


}
