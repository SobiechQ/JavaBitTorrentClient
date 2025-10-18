package ClientSession.Controller;

import Handshake.Service.HandshakeService;
import Model.DecodedBencode.Torrent;
import Peer.Controller.PeerController;

public interface ClientSessionController {
    void populateSessions(Torrent torrent);
}
