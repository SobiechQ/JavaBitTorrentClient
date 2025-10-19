package Handlers.Message;

import ClientSession.Service.ClientSessionService;
import Handlers.Service.HandlerService;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Slf4j
public class MessageInputHandler implements CompletionHandler<Integer, Object> {
    private final Torrent torrent;
    private final AsynchronousSocketChannel socket;
    private final Peer peer;
    private final ByteBuffer bufferIn;
    private final HandlerService handlerService;
    private final ClientSessionService clientSessionService;
    private final PeerService peerService;

    @Override
    public void completed(Integer bytesRead, Object attachment) {
        bufferIn.rewind();
        handlerService.handleMessageInput(torrent, peer, bufferIn, bytesRead);
        bufferIn.clear();
        socket.read(bufferIn, null, this);
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        log.warn("Unable to handle message from peer {}, exception: {}", this.peer,  exc.getMessage());
        clientSessionService.removeSession(torrent, peer);
        peerService.notifyFailed(torrent, peer);
    }
}
