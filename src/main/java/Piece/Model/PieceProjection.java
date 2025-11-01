package Piece.Model;

import Model.DecodedBencode.Torrent;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;

@Getter
public class PieceProjection {
    private final int index;
    private final long absoluteBegin;
    private final byte[] piece;

    public PieceProjection(@NonNull Torrent torrent, int index, byte[] piece) {
        this(index, getAbsoluteBegin(torrent, index), piece);
    }

    private PieceProjection(int index, long absoluteBegin, byte[] piece) {
        this.index = index;
        this.absoluteBegin = absoluteBegin;
        this.piece = piece;
    }

    private static long getAbsoluteBegin(@NonNull Torrent torrent, int index) {
        return Math.multiplyExact(torrent.getPieceLength(), (long) index);
    }

    @Override
    public String toString() {
        return "PieceProjection{" +
               "index=" + index +
               ", absoluteBegin=" + absoluteBegin +
               ", piece=" + piece.length +
               '}';
    }
}
