package Tracker.Controller;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerResponse;

import java.util.concurrent.CompletableFuture;

public interface TrackerController {
    CompletableFuture<TrackerResponse> asyncAnnounce(Torrent torrent);

}
