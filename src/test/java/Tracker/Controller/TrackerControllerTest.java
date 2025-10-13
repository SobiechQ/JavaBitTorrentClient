package Tracker.Controller;

import Model.DecodedBencode.Torrent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@SpringBootTest(classes = Configuration.Main.class)
@ContextConfiguration(classes = TrackerControllerImpl.class)
@Slf4j
class TrackerControllerTest {
    private final static String LOCATION = "src/test/java/resources/ubuntu-25.04-desktop-amd64.iso.torrent";
    private final static Torrent TORRENT;

    static {
        try {
            TORRENT = Torrent.fromFile(new File(LOCATION));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private TrackerController controller;

    @Test
    void subscribeAnnounce() throws ExecutionException, InterruptedException, TimeoutException {
        log.info("Start Async 1");
        final var futureResponse1 = controller.subscribeAnnounce(TORRENT).whenComplete((trackerResponse, throwable) -> {
            log.info("Async 1 finished with {}", trackerResponse);
            if (throwable != null) {
                log.warn("Async 1 failed", throwable);
            }
        });
        log.info("Start Async 2");
        final var futureResponse2 = controller.subscribeAnnounce(TORRENT).whenComplete((trackerResponse, throwable) -> {
            log.info("Async 2 finished with {}", trackerResponse);
            if (throwable != null) {
                log.warn("Async 2 failed", throwable);
            }
        });

        final var response1 = futureResponse1.get(10, TimeUnit.SECONDS);
        final var response2 = futureResponse2.get(10, TimeUnit.SECONDS);

        Assertions.assertEquals(response1.getRespondTo().getUri().toString(), "https://torrent.ubuntu.com/announce");
        Assertions.assertEquals(response2.getRespondTo().getUri().toString(), "https://ipv6.torrent.ubuntu.com/announce");

    }
}