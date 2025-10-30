package Configuration;

import ClientSession.Repository.ClientSessionRepository;
import Peer.Model.PeerStatisticProjection;
import Peer.Repository.PeerRepository;
import Peer.Service.PeerStrategyService;
import Piece.Event.PieceCompletedEvent;
import Piece.Repository.PieceRepository;
import Piece.Service.PieceService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static AsyncClient.AsyncClientTest.MOCK_TORRENT;

@Service
@AllArgsConstructor
@Slf4j
public class InformationServiceImpl implements InformationService {
    private final ScheduledExecutorService executor;
    private final ClientSessionRepository clientSessionRepository;
    private final PeerRepository peerRepository;
    private final PieceRepository pieceRepository;
    private final PieceService pieceService;
    private final PeerStrategyService peerStrategyService;

    @PostConstruct
    void start() {
        executor.scheduleAtFixedRate(() -> {
            final var sessions = clientSessionRepository.getSessions(MOCK_TORRENT);
            log.info("Active sessions: [{}]", sessions.size());

        }, 0, 1, TimeUnit.SECONDS);
        executor.scheduleAtFixedRate(() -> {
//            log.info("Failed count array: [{}]", peerRepository.getPeerStatisticProjection(MOCK_TORRENT)
//                    .map(PeerStatisticProjection::failedCount)
//                    .sorted(Comparator.comparingInt(i -> -i))
//                    .map(i -> Integer.toString(i))
//                    .collect(Collectors.joining(", ")));

            pieceRepository.getPieceStatusProjection(MOCK_TORRENT)
                    .filter(p -> p.downloaded() != 0)
                    .forEach(System.out::println);

        }, 0, 10, TimeUnit.SECONDS);
    }

    @Override
    public void logEvent(ApplicationEvent event) {
        log.info("Application Event {}", event);

    }
}
