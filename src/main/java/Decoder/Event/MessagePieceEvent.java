package Decoder.Event;

import Model.DecodedBencode.Torrent;
import Model.Message.MessageBitfield;
import Model.Message.MessagePiece;
import Model.Message.MessageProjection;
import Peer.Model.Peer;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.function.Consumer;

@Getter
@ToString(exclude = {"responseSender"}, callSuper = true)
public class MessagePieceEvent extends MessageEvent {
    private final MessagePiece piece;
    private final Consumer<MessageProjection> responseSender;

    public MessagePieceEvent(Object source, @NonNull Torrent torrent, @NonNull Peer peer,@NonNull MessagePiece piece,@NonNull Consumer<MessageProjection> responseSender) {
        super(source, torrent, peer);
        this.piece = piece;
        this.responseSender = responseSender;
    }
}
