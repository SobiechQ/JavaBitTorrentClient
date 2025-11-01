package Decoder.Event;

import Model.DecodedBencode.Torrent;
import Model.Message.MessageBitfield;
import Model.Message.MessageHave;
import Peer.Model.Peer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString(callSuper = true)
public class MessageBitfieldEvent extends MessageEvent {
    private final MessageBitfield bitfield;

    public MessageBitfieldEvent(Object source, @NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        super(source, torrent, peer);
        this.bitfield = bitfield;
    }
}
