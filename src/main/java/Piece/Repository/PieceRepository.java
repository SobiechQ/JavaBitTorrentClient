package Piece.Repository;

import Model.DecodedBencode.Torrent;
import lombok.NonNull;

import java.util.List;

public interface PieceRepository {
    void handlePiece(@NonNull Torrent torrent, int index, int begin, byte[] piece);
    boolean isPieceComplete(@NonNull Torrent torrent, int index);
    int getNextBegin(@NonNull Torrent torrent, int index);
    List<Integer> getNotStartedPieces(@NonNull Torrent torrent);
    List<Integer> getIncompletePieces(@NonNull Torrent torrent);
}
