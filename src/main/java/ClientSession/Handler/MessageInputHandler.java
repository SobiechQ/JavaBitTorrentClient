package ClientSession.Handler;

import ClientSession.Service.ClientSessionService;
import Decoder.Service.HandlerService;
import Model.DecodedBencode.Torrent;
import Model.Message.MessageProjection;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.List;
import java.util.function.Consumer;

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
        if (bytesRead == -1) {
            this.failed(new IllegalStateException("Connection closed"), bufferIn);
            return;
        }
        if (!handlerService.isMessageComplete(bufferIn, bytesRead)) {
            socket.read(bufferIn, null, this);
            return;
        }
        final var read = bufferIn.position();
        bufferIn.rewind();
        handlerService.handleMessageInput(torrent, peer, bufferIn, read, this::handleOutputMessages);
        bufferIn.clear();
        socket.read(bufferIn, null, this);
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        log.warn("Unable to handle message from peer {}, exception: {}", this.peer,  exc.getMessage());
        clientSessionService.removeSession(torrent, peer);
        peerService.notifyFailed(torrent, peer);
    }

    private void handleOutputMessages(MessageProjection message) {
        log.info("Sending to peer {} message {}", peer, message);
        final var data = message.getData();
        final var bufferOut = ByteBuffer.allocate(data.length);
        bufferOut.put(data);
        bufferOut.rewind();
        socket.write(bufferOut);
    }
}
