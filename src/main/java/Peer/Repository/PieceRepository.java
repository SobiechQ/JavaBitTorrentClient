package Peer.Repository;

import Message.Model.MessagePiece;
import Model.DecodedBencode.Torrent;
import Utils.RandomComparator;
import org.jooq.lambda.Seq;

import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class PieceRepository {
    private final static Comparator<PiecePart> PIECE_PART_COMPARATOR = Comparator.comparingInt(PiecePart::begin);
    private final Map<Integer, Set<PiecePart>> pieces;
    private final Torrent torrent;

    public PieceRepository(Torrent torrent) {
        this.pieces = new TreeMap<>(new RandomComparator<>());
        this.torrent = torrent;
    }

    public void handlePiece(MessagePiece messagePiece) {
        this.getPieceSet(messagePiece.getIndex()).add(toPiecePart(messagePiece));
    }

    public boolean isPieceComplete(int index) {
        Set<PiecePart> parts = getPieceSet(index);
        int offset = 0;
        for (PiecePart part : parts) {
            if (part.begin() != offset)
                return false;
            offset += part.piece().length;
        }
        return offset == torrent.getPieceLength();
    }

    public int getNextBegin(int index) {
        Set<PiecePart> parts = getPieceSet(index);
        if (parts.isEmpty()) return 0;

        int nextBegin = 0;
        for (PiecePart part : parts) {
            if (part.begin() > nextBegin)
                break;

            nextBegin = part.begin() + part.piece().length;
        }
        return nextBegin;
    }

    public Stream<Integer> getNotStartedPieces() {
        return IntStream.range(0, torrent.getPieceCount())
                .filter(this::isPieceNotStarted)
                .boxed();
    }

    public Stream<Integer> getIncompletePieces() {
        return this.pieces.keySet().stream()
                .filter(i -> !isPieceComplete(i));
    }

    private boolean isPieceNotStarted(int index) {
        return this.getPieceSet(index).isEmpty();
    }

    private PiecePart toPiecePart(MessagePiece messagePiece) {
        return new PiecePart(messagePiece.getBegin(), messagePiece.getPiece());
    }

    private Set<PiecePart> getPieceSet(int index) {
        return this.pieces.computeIfAbsent(index, _ -> new TreeSet<>(PIECE_PART_COMPARATOR));
    }


}
