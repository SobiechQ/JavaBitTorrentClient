package Integration;
import Configuration.Main;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
@SpringBootTest(classes = Main.class)
public class TrackerIntegrationTest {

    // Uruchomienie trackera w kontenerze
    static GenericContainer<?> tracker =
            new GenericContainer<>(DockerImageName.parse("lednerb/opentracker-docker"))
                    .withExposedPorts(6969, 6969);

    @BeforeAll
    void startTracker() {
        tracker.start();
        final var host = tracker.getHost();
        final var port = tracker.getMappedPort(6969);
        log.info("Tracker running on http://{}:{}/announce", host, port);

    }

    @AfterAll
    void stopTracker() {
        tracker.stop();
    }

    @Test
    void testTrackerIsReachable() {
        final var announceUrl = String.format("http://%s:%s/announce", tracker.getHost(), tracker.getMappedPort(6969));
        log.info("Announce URL: {}", announceUrl);
    }
}
