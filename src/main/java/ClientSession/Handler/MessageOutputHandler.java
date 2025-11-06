package ClientSession.Handler;

import ClientSession.Service.ClientSessionService;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class MessageOutputHandler implements CompletionHandler<Integer, Object> {
    private final Torrent torrent;
    private final AsynchronousSocketChannel socket;
    private final Peer peer;
    private final ClientSessionService clientSessionService;
    private final PeerService peerService;

    @Override
    public void completed(Integer result, Object attachment) {

    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        log.warn("Unable to send message peer {}, exception: {}", this.peer,  exc.getMessage());
        clientSessionService.removeSession(torrent, peer);
        peerService.notifyFailed(torrent, peer);
    }
}
