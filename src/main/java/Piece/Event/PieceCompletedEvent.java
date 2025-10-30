package Piece.Event;

import Model.DecodedBencode.Torrent;
import Piece.Model.PieceProjection;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.context.ApplicationEvent;

@Getter
public class PieceCompletedEvent extends ApplicationEvent {
    private final PieceProjection projection;
    private final Torrent torrent;

    public PieceCompletedEvent(Object source, @NonNull Torrent torrent, @NonNull PieceProjection projection) {
        super(source);
        this.projection = projection;
        this.torrent = torrent;
    }

    @Override
    public String toString() {
        return projection.toString();
    }
}
