package AsyncClient;

import ClientSession.Controller.ClientSessionController;
import Model.DecodedBencode.Torrent;
import Tracker.Controller.TrackerController;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;

@Controller
@AllArgsConstructor
public class AsyncClientTest {
    private final ClientSessionController sessionController;
    private final TrackerController trackerController;
    public final static Torrent MOCK_TORRENT;

    static {
        try {
            MOCK_TORRENT = Torrent.fromFile(new File("C:\\Users\\Sobiech\\Desktop\\Fedora-COSMIC-Atomic-ostree-aarch64-43.torrent"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void postConstruct() {
        trackerController.subscribeAnnounce(MOCK_TORRENT);
        sessionController.subscribeRepopulateSessions(MOCK_TORRENT);
    }
}
