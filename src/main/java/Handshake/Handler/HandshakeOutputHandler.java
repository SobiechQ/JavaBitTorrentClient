package Handshake.Handler;

import ClientSession.Service.ClientSessionService;
import Decoder.Service.DecoderService;
import Handshake.Model.HandshakeInputProjection;
import Handshake.Service.HandshakeService;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class HandshakeOutputHandler implements CompletionHandler<Void, Object> {
    private final Torrent torrent;
    private final AsynchronousSocketChannel socket;
    private final Peer peer;
    private final HandshakeService handshakeService;
    private final ClientSessionService clientSessionService;
    private final PeerService peerService;
    private final HandshakeHandlerFactory handshakeHandlerFactory;


    @Override
    public void completed(Void result, Object object) {
        log.info("Socket connected to peer {}", peer);
        final var handshake = handshakeService.getHandshake(torrent);
        final var bufferOut = ByteBuffer.allocate(handshake.handshake().length);
        bufferOut.put(handshake.handshake());
        bufferOut.rewind();
        socket.write(bufferOut);
        final var bufferIn = ByteBuffer.allocate(4096);
        socket.read(bufferIn, null, handshakeHandlerFactory.getHandshakeInputHandler(torrent, socket, peer, bufferIn));
    }

    @Override
    public void failed(Throwable exc, Object peerMessage) {
//        log.warn("Unable to connect to peer {}, exception: {}", this.peer,  exc.getMessage());
        clientSessionService.removeSession(torrent, peer);
        peerService.notifyFailed(torrent, peer);
    }
}
