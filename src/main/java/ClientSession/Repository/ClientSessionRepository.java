package ClientSession.Repository;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ClientSessionRepository {
    boolean addSession(Torrent torrent, AsynchronousSocketChannel socket, Peer peer);
    void removeSession(Torrent torrent, Peer peer);
    Set<Session> getSessions(Torrent torrent);
    Optional<Session> getSession(Torrent torrent, Peer peer);
}
