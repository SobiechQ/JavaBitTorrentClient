package Configuration;

import ClientSession.Repository.ClientSessionRepository;
import Decoder.Event.MessageEvent;
import Peer.Repository.PeerRepository;
import Peer.Service.PeerStrategyService;
import Piece.Repository.PieceRepository;
import Piece.Service.PieceService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static AsyncClient.AsyncClientTest.MOCK_TORRENT;

@Service
@RequiredArgsConstructor
@Slf4j
public class InformationServiceImpl implements InformationService {
    private final ScheduledExecutorService executor;
    private final ClientSessionRepository clientSessionRepository;
    private final PeerRepository peerRepository;
    private final PieceRepository pieceRepository;
    private final PieceService pieceService;
    private final PeerStrategyService peerStrategyService;
    @Value("${informationService.scheduled.enabled}")
    private boolean scheduledEnabled;
    @Value("${informationService.events.enabled}")
    private boolean eventsEnabled;
    @Value("${informationService.messageEvents.enabled}")
    private boolean messageEventsEnabled = true;

    @PostConstruct
    void start() {
        if (!scheduledEnabled)
            return;
        executor.scheduleAtFixedRate(() -> {
            final var sessions = clientSessionRepository.getSessions(MOCK_TORRENT);
            log.info("Active sessions: [{}]", sessions.size());

        }, 0, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(() -> {
            pieceRepository.getPieceStatusProjection(MOCK_TORRENT)
                    .filter(p -> p.downloaded() != 0)
                    .forEach(pieceStatusProjection -> log.info(pieceStatusProjection.toString()));

        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void logEvent(ApplicationEvent event) {
        if (!eventsEnabled)
            return;
        if (!messageEventsEnabled && event instanceof MessageEvent)
            return;
        log.info("Application Event {}", event);
    }
}
