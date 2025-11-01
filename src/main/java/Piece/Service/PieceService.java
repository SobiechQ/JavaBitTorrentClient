package Piece.Service;

import Decoder.Event.MessagePieceEvent;
import Model.Message.MessagePiece;
import Model.DecodedBencode.Torrent;
import Model.Message.MessageRequest;
import Peer.Model.Peer;
import Piece.Model.PieceProjection;
import lombok.NonNull;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;

public interface PieceService {
    Optional<MessageRequest> getRequest(Torrent torrent, Peer peer);
    Optional<MessageRequest> handlePiece(Torrent torrent, Peer peer, MessagePiece messagePiece);
    Optional<PieceProjection> getCompletedPiece(Torrent torrent, int index);

    @EventListener
    @Async
    default void handleMessagePieceEvent(@NonNull MessagePieceEvent messagePieceEvent) {
        this.handlePiece(messagePieceEvent.getTorrent(), messagePieceEvent.getPeer(), messagePieceEvent.getPiece())
                .ifPresent(messagePieceEvent.getResponseSender());
    }
}
