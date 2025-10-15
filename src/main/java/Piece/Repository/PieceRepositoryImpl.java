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
    private final Map<Torrent, ReentrantReadWriteLock> locks;

    public PieceRepositoryImpl() {
        this.pieceRepository = new HashMap<>();
        this.locks = new ConcurrentHashMap<>();
    }

    @Override
    public void handlePiece(@NonNull Torrent torrent, int index, int begin, byte[] piece) {
        final var lock = this.getLock(torrent).writeLock();
        try {
            lock.lock();
            this.getPieceRepositoryRecord(torrent).addPiecePart(index, begin, piece);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isPieceComplete(@NonNull Torrent torrent, int index) {
        final var lock = this.getLock(torrent).readLock();
        try {
            lock.lock();
            return this.getPieceRepositoryRecord(torrent).isPieceComplete(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int getNextBegin(@NonNull Torrent torrent, int index) {
        final var lock = this.getLock(torrent).readLock();
        try {
            lock.lock();
            return this.getPieceRepositoryRecord(torrent).getNextBegin(index);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Integer> getNotStartedPieces(@NonNull Torrent torrent) {
        final var lock = this.getLock(torrent).readLock();
        try {
            lock.lock();
            return this.getPieceRepositoryRecord(torrent)
                    .getNotStartedPieces();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<Integer> getIncompletePieces(@NonNull Torrent torrent) {
        final var lock = this.getLock(torrent).readLock();
        try {
            lock.lock();
            return this.getPieceRepositoryRecord(torrent)
                    .getIncompletePieces();
        } finally {
            lock.unlock();
        }
    }

    private synchronized ReentrantReadWriteLock getLock(@NonNull Torrent torrent) {
        return this.locks.computeIfAbsent(torrent, _ -> new ReentrantReadWriteLock());
    }

    private PieceRepositoryRecord getPieceRepositoryRecord(@NonNull Torrent torrent) {
        return this.pieceRepository.computeIfAbsent(torrent, _ -> new PieceRepositoryRecord(torrent));
    }


}
