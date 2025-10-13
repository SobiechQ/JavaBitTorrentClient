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
import java.util.stream.Stream;

@Controller
@AllArgsConstructor
@Slf4j
public class TrackerControllerImpl implements TrackerController {
    private final TrackerService service;
    private final ExecutorService virtualExecutor;
    private final ScheduledExecutorService scheduledExecutor;

    @Override
    public Stream<CompletableFuture<TrackerResponse>> asyncAnnounce(Torrent torrent) {
         return service.getRequests(torrent)
                .map(this::announce);
    }

    public void subscribeAsyncRevalidation(@NonNull Torrent torrent, Consumer<TrackerResponse> consumer) {
        scheduledExecutor.scheduleAtFixedRate(() -> this.asyncAnnounce(torrent)
                .forEach(futureResponse -> futureResponse
                        .thenAccept(consumer)
                        .exceptionally(ex -> {
                            log.warn("Tracker announce failed", ex);
                            return null;
                        })
                ), 1,  1, TimeUnit.MINUTES);

        scheduledExecutor.scheduleAtFixedRate(() -> service.removeUnreachableTrackers(torrent), 10, 5, TimeUnit.MINUTES);
    }

    private CompletableFuture<TrackerResponse> announce(TrackerRequestProjection projection) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final var resp = projection.tracker().announce(projection);
                this.service.notifySuccess(resp);
                return resp;
            } catch (IOException e) {
                this.service.notifyFailure(projection.tracker());
                throw new RuntimeException(e);
            }
        }, virtualExecutor);
    }


}
