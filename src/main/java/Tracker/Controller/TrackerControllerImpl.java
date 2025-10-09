package Tracker.Controller;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerResponse;
import Tracker.Service.TrackerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.concurrent.*;

@Controller
@AllArgsConstructor
public class TrackerControllerImpl implements TrackerController {
    private final TrackerService service;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public CompletableFuture<TrackerResponse> asyncAnnounce(Torrent torrent) {
        return CompletableFuture.supplyAsync(() -> announce(torrent), executor);
    }

    private TrackerResponse announce(Torrent torrent) {
        final var request = service.getRequest(torrent);
        try {
            return request.tracker().announce(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
