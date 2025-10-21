package Piece.Repository;

import Model.DecodedBencode.Torrent;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class PieceRepositoryRecord {
    private final static Comparator<PiecePart> PIECE_PART_COMPARATOR = Comparator.comparingInt(PiecePart::begin);
    private final Map<Integer, Set<PiecePart>> pieces;
    private final Map<Integer, ReentrantReadWriteLock> locks;
    private final Torrent torrent;

    PieceRepositoryRecord(@NonNull Torrent torrent) {
        this.pieces = new HashMap<>();
        locks = new ConcurrentHashMap<>();
        this.torrent = torrent;
    }

    void addPiecePart(int index, int begin, byte[] piece) {
        final var lock = this.getLock(index).writeLock();
        try {
            lock.lock();
            this.getPieceSet(index).add(new PiecePart(begin, piece));
        } finally {
            lock.unlock();
        }
    }

    boolean isPieceComplete(int index) {
        final var lock = this.getLock(index).readLock();
        try {
            lock.lock();
            Set<PiecePart> parts = getPieceSet(index);
            int offset = 0;
            for (PiecePart part : parts) {
                if (part.begin() != offset)
                    return false;
                offset += part.piece().length;
            }
            return offset == this.getPieceLength(index);
        } finally {
            lock.unlock();
        }
    }

    int getNextBegin(int index) {
        final var lock = this.getLock(index).readLock();
        try {
            lock.lock();
            Set<PiecePart> parts = getPieceSet(index);
            if (parts.isEmpty()) return 0;
            int nextBegin = 0;
            for (PiecePart part : parts) {
                if (part.begin() > nextBegin)
                    break;

                nextBegin = part.begin() + part.piece().length;
            }
            return nextBegin;
        } finally {
            lock.unlock();
        }
    }

    Set<Integer> getPieces() {
        return this.pieces.keySet();
    }

    List<Integer> getNotStartedPieces() { //todo set<>?
        return IntStream.range(0, torrent.getPieceCount())
                .filter(this::isPieceNotStarted)
                .boxed()
                .toList();
    }

    List<Integer> getIncompletePieces() {
        return this.pieces.keySet().stream()
                .filter(i -> !isPieceComplete(i))
                .toList();
    }

    Set<PiecePart> getPieceSet(int index) {
        return this.pieces.computeIfAbsent(index, _ -> new TreeSet<>(PIECE_PART_COMPARATOR));
    }

    int getPieceLength(int index) {
        final var length = torrent.getPieceLength();
        final var count = torrent.getPieceCount();
        if (index < count - 1)
            return length;

        return (int) (torrent.getLength() - ((long) length * (count - 1)));
    }

    private boolean isPieceNotStarted(int index) {
        return this.getPieceSet(index).isEmpty();
    }

    private synchronized ReentrantReadWriteLock getLock(int index) {
        return this.locks.computeIfAbsent(index, _ -> new ReentrantReadWriteLock());
    }
}
