package ClientSession.Controller;

import ClientSession.Service.ClientSessionService;
import Handlers.Handshake.HandshakeHandlerFactory;
import Model.DecodedBencode.Torrent;
import Peer.Controller.PeerController;
import Peer.Model.Peer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

@AllArgsConstructor
@Controller
@Slf4j
public class ClientSessionControllerImpl implements ClientSessionController {
    private final ClientSessionService clientSessionService;
    private final PeerController peerController;
    private final HandshakeHandlerFactory handshakeHandlerFactory;
    private final ScheduledExecutorService scheduledExecutorService;

    @Override
    public void subscribeRepopulateSessions(@NonNull Torrent torrent) {
        scheduledExecutorService.scheduleAtFixedRate(() -> this.repopulateSessions(torrent),
                5,
                30,
                SECONDS);
        log.info("Subscribed to repopulate task");
    }

    private void repopulateSessions(@NonNull Torrent torrent) {
        log.info("Session repopulate task started {}", peerController.getPeers(torrent).count());

        final var socketPeer = peerController.getPeers(torrent)
                .map(p -> this.toSocketPeer(torrent, p))
                .flatMap(Optional::stream);
        clientSessionService.populateSessions(torrent, socketPeer);
    }

    private Optional<Tuple2<AsynchronousSocketChannel, Peer>> toSocketPeer(@NonNull Torrent torrent, @NonNull Peer peer) {
        try {
            log.info("Opening socket to peer {}", peer);
            final var socket = AsynchronousSocketChannel.open();
            socket.connect(peer.getInetSocketAddress(), null, handshakeHandlerFactory.getHandshakeOutputHandler(torrent, socket, peer));
            return Optional.of(Tuple.tuple(socket, peer));
        } catch (IOException e) {
            log.warn("Unable to establish connection to peer {}", peer);
            return Optional.empty();
        }
    }
}
