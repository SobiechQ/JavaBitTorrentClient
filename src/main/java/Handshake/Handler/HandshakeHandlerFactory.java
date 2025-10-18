package Handshake.Handler;

import ClientSession.Service.ClientSessionService;
import Decoder.Service.DecoderService;
import Handshake.Service.HandshakeService;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.nio.channels.AsynchronousSocketChannel;

@AllArgsConstructor
@Component
public class HandshakeHandlerFactory {
    private final HandshakeService handshakeService;
    private final DecoderService decoderService;
    private final ClientSessionService clientSessionService;


    public HandshakeOutputHandler getHandshakeOutputHandler(@NonNull Torrent torrent, @NonNull AsynchronousSocketChannel socket, @NonNull Peer peer) {
        return new HandshakeOutputHandler(
                torrent,
                socket,
                peer,
                handshakeService,
                clientSessionService,
                this
        );
    }

    public HandshakeInputHandler getHandshakeInputHandler(@NonNull Torrent torrent, @NonNull AsynchronousSocketChannel socket, @NonNull Peer peer) {
        return new HandshakeInputHandler(
                torrent,
                socket,
                peer,
                handshakeService,
                decoderService,
                clientSessionService
        );
    }
}
