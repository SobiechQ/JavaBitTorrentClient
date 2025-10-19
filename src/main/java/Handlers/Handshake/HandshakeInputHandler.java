package Handlers.Handshake;

import ClientSession.Service.ClientSessionService;
import Handlers.Service.DecoderService;
import Handlers.Message.MessageHandlerFactory;
import Handlers.Service.HandlerService;
import Handshake.Service.HandshakeService;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import static Model.Message.MessageRequest.REQUEST_LENGTH;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class HandshakeInputHandler implements CompletionHandler<Integer, Object> {
    private final Torrent torrent;
    private final AsynchronousSocketChannel socket;
    private final Peer peer;
    private final ByteBuffer bufferIn;
    private final HandshakeService handshakeService;
    private final DecoderService decoderService;
    private final ClientSessionService clientSessionService;
    private final PeerService peerService;
    private final MessageHandlerFactory messageHandlerFactory;
    private final HandlerService handlerService;


    @Override
    public void completed(Integer bytesRead, Object object) {
        if (bytesRead < 68) {
            this.failed(new IllegalStateException("Input handshake too short"), bufferIn);
            return;
        }
        final var handshakeVerified = decoderService.decodeHandshake(bufferIn)
                .map(h -> handshakeService.verifyHandshake(torrent, h))
                .orElse(false);

        if (!handshakeVerified) {
            this.failed(new IllegalStateException("Handshake verification failed"), bufferIn);
            return;
        }

        log.info("Handshake established with peer {}", peer);
        handlerService.handleMessageInput(torrent, peer, bufferIn, bytesRead - 68);
        final var messageBuffer = ByteBuffer.allocate(REQUEST_LENGTH);
        socket.read(messageBuffer, null, messageHandlerFactory.getMessageInputHandler(torrent, socket, peer, messageBuffer));
    }


    @Override
    public void failed(Throwable exc, Object object) {
        log.warn("Unable to handle handshake from peer {}, exception: {}", this.peer,  exc.getMessage());
        clientSessionService.removeSession(torrent, peer);
        peerService.notifyFailed(torrent, peer);
    }
}
