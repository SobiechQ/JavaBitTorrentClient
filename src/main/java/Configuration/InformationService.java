package Configuration;

import ClientSession.Repository.ClientSessionRepository;
import Peer.Model.PeerStatisticProjection;
import Peer.Repository.PeerRepository;
import Peer.Service.PeerStrategyService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static AsyncClient.AsyncClientTest.MOCK_TORRENT;

@Service
@AllArgsConstructor
public class InformationService {
    private static final Logger log = LoggerFactory.getLogger(InformationService.class);
    private final ScheduledExecutorService executor;
    private final ClientSessionRepository clientSessionRepository;
    private final PeerRepository peerRepository;
    private final PeerStrategyService peerStrategyService;

    @PostConstruct
    void start() {
        executor.scheduleAtFixedRate(() -> {
            final var sessions = clientSessionRepository.getSessions(MOCK_TORRENT);
            log.info("Active sessions: [{}]", sessions.size());

        }, 0, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(() -> {
            log.info("Failed count array: [{}]", peerRepository.getPeerStatisticProjection(MOCK_TORRENT)
                    .map(PeerStatisticProjection::failedCount)
                    .sorted(Comparator.comparingInt(i -> -i))
                    .map(i -> Integer.toString(i))
                    .collect(Collectors.joining(", ")));

        }, 0, 10, TimeUnit.SECONDS);
    }
}
