package Piece.Repository;

import Model.DecodedBencode.Torrent;
import Piece.Model.PieceProjection;
import Utils.ByteUtils;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.IntFunction;
import java.util.stream.Stream;

@Repository
public class PieceRepositoryImpl implements PieceRepository {
    private final Map<Torrent, PieceRepositoryRecord> pieceRepository;

    public PieceRepositoryImpl() {
        this.pieceRepository = new HashMap<>();
    }

    @Override
    public void handlePiece(@NonNull Torrent torrent, int index, int begin, byte[] piece) {
        this.getPieceRepositoryRecord(torrent).addPiecePart(index, begin, piece);
    }

    @Override
    public boolean isPieceComplete(@NonNull Torrent torrent, int index) {
        return this.getPieceRepositoryRecord(torrent).isPieceComplete(index);
    }

    @Override
    public int getNextBegin(@NonNull Torrent torrent, int index) {
        return this.getPieceRepositoryRecord(torrent).getNextBegin(index);
    }

    @Override
    public PieceProjection getPieceProjection(@NonNull Torrent torrent, int index) {
        final var pieceSet = this.getPieceRepositoryRecord(torrent).getPieceSet(index);
        return this.toProjection(torrent, index, pieceSet);
    }

    @Override
    public Stream<PieceProjection> getPieceProjection(@NonNull Torrent torrent) {
        final var record = this.getPieceRepositoryRecord(torrent);
        return record
                .getPieces()
                .stream()
                .map(i -> this.toProjection(torrent, i, record.getPieceSet(i)));
    }

    @Override
    public byte[] getCompletedPiece(@NonNull Torrent torrent, int index) {
        if (!this.isPieceComplete(torrent, index)) {
            throw new IllegalStateException("Piece is not complete");
        }
        final var bytes = this.getPieceRepositoryRecord(torrent)
                .getPieceSet(index)
                .stream()
                .map(PiecePart::piece)
                .flatMap(ByteUtils::bytesToStream)
                .toList();
        return ByteUtils.unbox(bytes);
    }

    @Override
    public int getPieceLength(@NonNull Torrent torrent, int index) {
        return this.getPieceRepositoryRecord(torrent).getPieceLength(index);
    }

    @Override
    public List<Integer> getNotStartedPieces(@NonNull Torrent torrent) {
        return this.getPieceRepositoryRecord(torrent).getNotStartedPieces();
    }

    @Override
    public List<Integer> getIncompletePieces(@NonNull Torrent torrent) {
        return this.getPieceRepositoryRecord(torrent).getIncompletePieces();
    }

    private PieceRepositoryRecord getPieceRepositoryRecord(@NonNull Torrent torrent) {
        return this.pieceRepository.computeIfAbsent(torrent, _ -> new PieceRepositoryRecord(torrent));
    }

    private PieceProjection toProjection(@NonNull Torrent torrent, int index, @NonNull Set<PiecePart> parts) {
        final var downloaded = parts.stream()
                .map(piecePart -> piecePart.piece().length)
                .mapToInt(i -> i)
                .sum();

        return new PieceProjection(index, downloaded, this.getPieceRepositoryRecord(torrent).getPieceLength(index) - downloaded);
    }


}
