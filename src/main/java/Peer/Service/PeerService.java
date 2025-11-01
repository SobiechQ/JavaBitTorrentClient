package Peer.Service;

import Decoder.Event.MessageBitfieldEvent;
import Decoder.Event.MessageHaveEvent;
import Model.Message.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Model.Message.MessageHave;
import Peer.Model.Peer;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public interface PeerService {
    void notifyFailed(Torrent torrent, Peer peer);
    void handleBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
    void handleHave(Torrent torrent, Peer peer, MessageHave have);
    boolean isPieceAvailable(Torrent torrent, Peer peer, int index);

    @EventListener
    @Async
    default void handleMessageHaveEvent(@NotNull MessageHaveEvent messageHaveEvent) {
        this.handleHave(messageHaveEvent.getTorrent(), messageHaveEvent.getPeer(), messageHaveEvent.getHave());
    }

    @EventListener
    @Async
    default void handleMessageBitfieldEvent(@NotNull MessageBitfieldEvent messageBitfieldEvent) {
        this.handleBitfield(messageBitfieldEvent.getTorrent(), messageBitfieldEvent.getPeer(), messageBitfieldEvent.getBitfield());
    }
}
