package ClientSession.Controller;

import Model.DecodedBencode.Torrent;

public interface ClientSessionController {
    void subscribeRepopulateSessions(Torrent torrent);
}
