package AsyncClient;

import ClientSession.Controller.ClientSessionController;
import Model.DecodedBencode.Torrent;
import Tracker.Controller.TrackerController;
import io.vavr.control.Try;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@AllArgsConstructor
@Slf4j
public class AsyncClientTest {
    private final ClientSessionController sessionController;
    private final TrackerController trackerController;
    public final static Optional<Torrent> MOCK_TORRENT;

    static {
        MOCK_TORRENT = Try.of(() -> Torrent.fromFile(new File("docker/transmission/downloads/file.txt.torrent")))
                .onFailure((_) -> log.warn("Mock torrent file not loaded"))
                .toOption().toJavaOptional();
    }

//    @PostConstruct
//    public void postConstruct() {
//        trackerController.subscribeAnnounce(MOCK_TORRENT);
//        sessionController.subscribeRepopulateSessions(MOCK_TORRENT);
//    }
}
