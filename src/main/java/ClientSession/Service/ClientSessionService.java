package ClientSession.Service;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import org.jooq.lambda.tuple.Tuple2;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.stream.Stream;

public interface ClientSessionService {
    void populateSessions(Torrent torrent, Stream<Tuple2<AsynchronousSocketChannel, Peer>> socketPeer);
    void removeSession(Torrent torrent, Peer peer);
}
