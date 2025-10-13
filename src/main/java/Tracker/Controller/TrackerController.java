package Tracker.Controller;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerResponse;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface TrackerController {
    void subscribeAnnounce(Torrent torrent, Consumer<TrackerResponse> consumer);
    void subscribeAsyncRevalidation(Torrent torrent, Consumer<TrackerResponse> consumer);
}
