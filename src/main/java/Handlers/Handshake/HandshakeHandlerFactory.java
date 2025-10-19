package Handlers.Handshake;

import ClientSession.Service.ClientSessionService;
import Handlers.Service.DecoderService;
import Handlers.Message.MessageHandlerFactory;
import Handlers.Service.HandlerService;
import Handshake.Service.HandshakeService;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

@AllArgsConstructor
@Component
public class HandshakeHandlerFactory {
    private final HandshakeService handshakeService;
    private final DecoderService decoderService;
    private final ClientSessionService clientSessionService;
    private final PeerService peerService;
    private final MessageHandlerFactory messageHandlerFactory;
    private final HandlerService handlerService;


    public HandshakeOutputHandler getHandshakeOutputHandler(@NonNull Torrent torrent, @NonNull AsynchronousSocketChannel socket, @NonNull Peer peer) {
        return new HandshakeOutputHandler(
                torrent,
                socket,
                peer,
                handshakeService,
                clientSessionService,
                peerService,
                this
        );
    }

    public HandshakeInputHandler getHandshakeInputHandler(@NonNull Torrent torrent, @NonNull AsynchronousSocketChannel socket, @NonNull Peer peer, @NonNull ByteBuffer bufferIn) {
        return new HandshakeInputHandler(
                torrent,
                socket,
                peer,
                bufferIn,
                handshakeService,
                decoderService,
                clientSessionService,
                peerService,
                messageHandlerFactory,
                handlerService
        );
    }
}
