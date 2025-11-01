package Decoder.Event;

import Model.DecodedBencode.Torrent;
import Model.Message.MessageHave;
import Peer.Model.Peer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString(callSuper = true)
public class MessageHaveEvent extends MessageEvent {
    private final MessageHave have;

    public MessageHaveEvent(Object source, @NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageHave have) {
        super(source, torrent, peer);
        this.have = have;
    }
}
