package Piece.Repository;

import Model.DecodedBencode.Torrent;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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


}
