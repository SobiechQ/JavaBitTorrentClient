package ClientSession.Service;

import ClientSession.Repository.ClientSessionRepository;
import Handshake.Service.HandshakeService;
import Model.DecodedBencode.Torrent;
import Peer.Controller.PeerController;
import Peer.Model.Peer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.stereotype.Service;

import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ClientSessionServiceImpl implements ClientSessionService {
    private final ClientSessionRepository clientSessionRepository;

    @SuppressWarnings("unchecked")
    @Override
    public void populateSessions(@NonNull Torrent torrent, @NonNull Stream<Tuple2<AsynchronousSocketChannel, Peer>> socketPeer) {
        Seq.ofType(socketPeer, Tuple2.class)
                .map(sp -> (Tuple2<AsynchronousSocketChannel, Peer>) sp)
                .map(sp -> clientSessionRepository.addSession(torrent, sp.v1,sp.v2))
                .limitWhile(b -> b)
                .forEach(_ -> {});
    }

    @Override
    public void removeSession(@NonNull Torrent torrent,@NonNull Peer peer) {
        this.clientSessionRepository.removeSession(torrent, peer);
    }
}
