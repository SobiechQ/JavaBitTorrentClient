package ClientSession.Controller;

import ClientSession.Service.ClientSessionService;
import Decoder.Service.DecoderService;
import Handshake.Handler.HandshakeHandlerFactory;
import Handshake.Handler.HandshakeOutputHandler;
import Handshake.Service.HandshakeService;
import Model.DecodedBencode.Torrent;
import Peer.Controller.PeerController;
import Peer.Model.Peer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Optional;

@AllArgsConstructor
@Controller
public class ClientSessionControllerImpl implements ClientSessionController {
    private static final Logger log = LoggerFactory.getLogger(ClientSessionControllerImpl.class);
    private final ClientSessionService clientSessionService;
    private final PeerController peerController;
    private final HandshakeHandlerFactory handshakeHandlerFactory;

    @Override
    public void populateSessions(@NonNull Torrent torrent) {
         final var socketPeer = peerController.getPeers(torrent)
                 .map(p -> this.toSocketPeer(torrent, p))
                 .flatMap(Optional::stream);
         clientSessionService.populateSessions(torrent, socketPeer);
    }

    private Optional<Tuple2<AsynchronousSocketChannel, Peer>> toSocketPeer(@NonNull Torrent torrent, @NonNull Peer peer) {
        try {
            final var socket = AsynchronousSocketChannel.open();
            socket.connect(peer.getInetSocketAddress(), null, handshakeHandlerFactory.getHandshakeOutputHandler(torrent, socket, peer));
            return Optional.of(Tuple.tuple(socket, peer));
        } catch (IOException e) {
            log.warn("Unable to establish connection to peer {}", peer);
            return Optional.empty();
        }
    }
}
