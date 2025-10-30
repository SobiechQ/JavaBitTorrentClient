package Piece.Repository;

import Model.DecodedBencode.Torrent;
import Piece.Model.PieceProjection;
import Piece.Model.PieceStatusProjection;
import Utils.ByteUtils;
import com.google.common.hash.Hashing;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Repository
public class PieceRepositoryImpl implements PieceRepository {
    private final Map<Torrent, PieceRepositoryRecord> pieceRepository;

    public PieceRepositoryImpl() {
        this.pieceRepository = new ConcurrentHashMap<>();
    }

    @Override
    public void handlePiece(@NonNull Torrent torrent, int index, int begin, byte[] piece) {
        this.getPieceRepositoryRecord(torrent).addPiecePart(index, begin, piece);
    }

    @Override
    public boolean isPieceComplete(@NonNull Torrent torrent, int index) {
        if (this.getPieceRepositoryRecord(torrent).isPieceComplete(index)){
            final var piece = this.getPiece(torrent, index).piece();
            return verifyHash(torrent, index, piece);
        }
        return false;
    }

    @Override
    public int getNextBegin(@NonNull Torrent torrent, int index) {
        return this.getPieceRepositoryRecord(torrent).getNextBegin(index);
    }

    @Override
    public PieceStatusProjection getPieceStatusProjection(@NonNull Torrent torrent, int index) {
        final var pieceSet = this.getPieceRepositoryRecord(torrent).getPieceSet(index);
        return this.toProjection(torrent, index, pieceSet);
    }

    @Override
    public Stream<PieceStatusProjection> getPieceStatusProjection(@NonNull Torrent torrent) {
        final var record = this.getPieceRepositoryRecord(torrent);
        return record
                .getPieces()
                .stream()
                .map(i -> this.toProjection(torrent, i, record.getPieceSet(i)));
    }

    @Override
    public PieceProjection getPiece(@NonNull Torrent torrent, int index) {
        final var pieceParts = this.getPieceRepositoryRecord(torrent).getPieceSet(index);
        final var begin = pieceParts.stream().findFirst().map(PiecePart::begin);
        final var bytes = pieceParts
                .stream()
                .map(PiecePart::piece)
                .flatMap(ByteUtils::bytesToStream)
                .toList();

        return begin.map(i -> new PieceProjection(index, i, ByteUtils.unbox(bytes))).orElseThrow(() -> new IllegalStateException("Piece is not complete"));
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

    private PieceStatusProjection toProjection(@NonNull Torrent torrent, int index, @NonNull Set<PiecePart> parts) {
        final var downloaded = parts.stream()
                .map(piecePart -> piecePart.piece().length)
                .mapToInt(i -> i)
                .sum();

        return new PieceStatusProjection(index, downloaded, this.getPieceRepositoryRecord(torrent).getPieceLength(index) - downloaded);
    }

    private boolean verifyHash(@NonNull Torrent torrent, int index, byte[] piece) {
        final var checksum = torrent.getPieceHash(index);
        final var calculated = Hashing.sha1().hashBytes(piece).asBytes();
        return checksum.map(h -> Arrays.equals(h, calculated)).orElse(false);
    }


}
