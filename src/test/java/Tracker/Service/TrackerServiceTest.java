package Tracker.Service;

import Model.DecodedBencode.Torrent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@SpringBootTest(classes = Configuration.Main.class)
@ContextConfiguration(classes = TrackerServiceImpl.class)
class TrackerServiceTest {

    private final static List<List<URI>> MOCK_ANNOUNCE_LIST = List.of(
            List.of(URI.create("http://t0e0"), URI.create("http://t0e1"), URI.create("http://t0e2")),
            List.of(URI.create("http://t1e0"), URI.create("http://t1e1")),
            List.of(URI.create("http://t2e0"))
    );

    private final static Torrent MOCK_TORRENT;

    static {
        MOCK_TORRENT = mock(Torrent.class);
        when(MOCK_TORRENT.getAnnounceList()).then((Answer<Stream<List<URI>>>) _ -> MOCK_ANNOUNCE_LIST.stream());
        when(MOCK_TORRENT.isAnnounceListAvailable()).then(_ -> true);
        when(MOCK_TORRENT.getLength()).then(_ -> 20000L);
        when(MOCK_TORRENT.getInfoHashUrl()).then(_ -> "infoHashUrl");
    }

    @Autowired
    private TrackerService service;

    @Test
    void computeUploaded() {
        final var uploadedBefore = service.getRequest(MOCK_TORRENT).uploaded();
        service.computeUploaded(MOCK_TORRENT, l -> l + 1000);
        final var uploadedAfter = service.getRequest(MOCK_TORRENT).uploaded();

        Assertions.assertEquals(uploadedBefore + 1000, uploadedAfter);
    }

    @Test
    void computeDownloaded() {
        final var downloadedBefore = service.getRequest(MOCK_TORRENT).downloaded();
        service.computeDownloaded(MOCK_TORRENT, l -> l + 1000);
        final var downloadedAfter = service.getRequest(MOCK_TORRENT).downloaded();

        Assertions.assertEquals(downloadedBefore + 1000, downloadedAfter);
    }

    @Test
    void computeLeft() {
        final var leftBefore = service.getRequest(MOCK_TORRENT).left();
        service.computeLeft(MOCK_TORRENT, l -> l - 1000);
        final var leftAfter = service.getRequest(MOCK_TORRENT).left();

        Assertions.assertEquals(leftBefore - 1000, leftAfter);
    }

    @Test
    void notifySuccessAndFailure() {
        Supplier<URI> getUri = () -> {
            try {
                return service.getRequest(MOCK_TORRENT).url().toURI();
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        };

        Assertions.assertEquals(MOCK_ANNOUNCE_LIST.getFirst().get(0), getUri.get());
        service.notifyFailure(MOCK_TORRENT);
        Assertions.assertEquals(MOCK_ANNOUNCE_LIST.getFirst().get(1), getUri.get());
        service.notifyFailure(MOCK_TORRENT);
        Assertions.assertEquals(MOCK_ANNOUNCE_LIST.getFirst().get(2), getUri.get());
        service.notifySuccess(MOCK_TORRENT);
        Assertions.assertEquals(MOCK_ANNOUNCE_LIST.getFirst().get(2), getUri.get());
        service.notifyFailure(MOCK_TORRENT);
        service.notifyFailure(MOCK_TORRENT);
        service.notifyFailure(MOCK_TORRENT);
        Assertions.assertEquals(MOCK_ANNOUNCE_LIST.get(1).get(0), getUri.get());
        service.notifyFailure(MOCK_TORRENT);
        Assertions.assertEquals(MOCK_ANNOUNCE_LIST.get(1).get(1), getUri.get());
        service.notifyFailure(MOCK_TORRENT);
        service.notifyFailure(MOCK_TORRENT);
        Assertions.assertEquals(MOCK_ANNOUNCE_LIST.getFirst().get(2), getUri.get());
    }
}