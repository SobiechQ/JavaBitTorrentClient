package Piece.Repository;

import Model.DecodedBencode.Torrent;
import Piece.Model.PieceProjection;
import Piece.Model.PieceStatusProjection;

import java.util.List;
import java.util.stream.Stream;

public interface PieceRepository {
    void handlePiece(Torrent torrent, int index, int begin, byte[] piece);
    boolean isPieceComplete(Torrent torrent, int index);
    int getNextBegin(Torrent torrent, int index);
    PieceStatusProjection getPieceStatusProjection(Torrent torrent, int index);
    Stream<PieceStatusProjection> getPieceStatusProjection(Torrent torrent);
    PieceProjection getPiece(Torrent torrent, int index);
    int getPieceLength(Torrent torrent, int index);
    List<Integer> getNotStartedPieces(Torrent torrent);
    List<Integer> getIncompletePieces(Torrent torrent);
}
