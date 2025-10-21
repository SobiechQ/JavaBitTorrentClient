package Piece.Repository;

import Model.DecodedBencode.Torrent;
import lombok.NonNull;

import java.util.List;

public interface PieceRepository {
    void handlePiece(Torrent torrent, int index, int begin, byte[] piece);
    boolean isPieceComplete(Torrent torrent, int index);
    int getNextBegin(Torrent torrent, int index);
    List<Integer> getNotStartedPieces(Torrent torrent);
    List<Integer> getIncompletePieces(Torrent torrent);
}
