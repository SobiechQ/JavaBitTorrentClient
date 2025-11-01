package File.Service;

import Model.DecodedBencode.Torrent;
import Piece.Event.PieceCompletedEvent;
import Piece.Model.PieceProjection;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public interface FileService {
    void handleCompletedPiece(Torrent torrent, PieceProjection pieceProjection);

    @EventListener
    @Async
    default void handlePieceCompletedEvent(@NotNull PieceCompletedEvent event) {
        this.handleCompletedPiece(event.getTorrent(), event.getProjection());
    }
}
