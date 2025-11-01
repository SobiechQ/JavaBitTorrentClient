package Decoder.Event;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

@Getter
@ToString(exclude = "torrent")
public abstract class MessageEvent extends ApplicationEvent {
    private final Torrent torrent;
    private final Peer peer;

    public MessageEvent(Object source, @NonNull Torrent torrent, @NonNull Peer peer) {
        super(source);
        this.torrent = torrent;
        this.peer = peer;
    }
}
