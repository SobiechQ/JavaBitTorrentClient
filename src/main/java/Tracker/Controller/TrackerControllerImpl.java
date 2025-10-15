package Tracker.Controller;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Messages.TrackerResponse;
import Tracker.Service.TrackerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Controller
@AllArgsConstructor
@Slf4j
public class TrackerControllerImpl implements TrackerController {
    private final TrackerService trackerService;
    private final ExecutorService virtualExecutor;
    private final ScheduledExecutorService scheduledExecutor;

    @Override
    public void subscribeAnnounce(@NonNull Torrent torrent, Consumer<TrackerResponse> consumer) {
        trackerService.getRequests(torrent)
                .map(this::announce)
                .map(cf -> cf.exceptionally(ex -> {
                    log.warn("Tracker announce failed", ex);
                    return null;
                }))
                .forEach(cf -> cf.thenAccept(consumer));
    }

    public void subscribeAsyncRevalidation(@NonNull Torrent torrent, Consumer<TrackerResponse> consumer) {
        scheduledExecutor.scheduleAtFixedRate(() -> {
            trackerService.getScheduledRequests(torrent)
                    .map(this::announce)
                    .map(cf -> cf.exceptionally(ex -> {
                        log.warn("Tracker announce failed", ex);
                        return null;
                    }))
                    .forEach(cf -> cf.thenAccept(consumer));
        }, 1, 1, TimeUnit.MINUTES);

        scheduledExecutor.scheduleAtFixedRate(() -> trackerService.removeUnreachableTrackers(torrent), 10, 5, TimeUnit.MINUTES);
    }

    private CompletableFuture<TrackerResponse> announce(TrackerRequestProjection projection) {
        return CompletableFuture.supplyAsync(() -> {
                    try {
                        return projection.tracker().announce(projection);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, virtualExecutor)
                .whenComplete(((response, _) -> {
                    if (response != null) {
                        this.trackerService.notifySuccess(response);
                        return;
                    }
                    this.trackerService.notifyFailure(projection.tracker());
                }));
    }


}
