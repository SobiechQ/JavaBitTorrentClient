package Handlers.Message;

import ClientSession.Service.ClientSessionService;
import Handlers.Service.DecoderService;
import Handlers.Service.HandlerService;
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
public class MessageHandlerFactory {
    private final HandlerService handlerService;
    private final ClientSessionService clientSessionService;
    private final PeerService peerService;

    public MessageInputHandler getMessageInputHandler(@NonNull Torrent torrent, @NonNull AsynchronousSocketChannel socket, @NonNull Peer peer, @NonNull ByteBuffer buffer) {
        return new MessageInputHandler(
                torrent,
                socket,
                peer,
                buffer,
                handlerService,
                clientSessionService,
                peerService
        );
    }
}