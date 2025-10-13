package Tracker.Controller;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Tracker.Model.Messages.TrackerResponse;
import Tracker.Model.Tracker;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface TrackerController {
    Stream<CompletableFuture<TrackerResponse>> asyncAnnounce(Torrent torrent);
    void subscribeAsyncRevalidation(Torrent torrent, Consumer<TrackerResponse> consumer);
}
