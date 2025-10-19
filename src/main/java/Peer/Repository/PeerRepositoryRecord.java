package Peer.Repository;

import Model.DecodedBencode.Torrent;
import Model.Message.MessageBitfield;
import Peer.Model.Peer;
import Peer.Model.PeerStatisticProjection;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;


class PeerRepositoryRecord {
    private final Map<Peer, PeerStatistic> peers;
    private final Map<Peer, ReentrantReadWriteLock> locks;

    PeerRepositoryRecord() {
        this.peers = new HashMap<>();
        this.locks = new ConcurrentHashMap<>();
    }

    Set<PeerStatisticProjection> getPeers() {
        synchronized (this.peers) {
            return peers.values().stream()
                    .map(this::toPeerStatisticProjection)
                    .collect(Collectors.toUnmodifiableSet());
        }
    }

    void updateFailed(@NonNull Peer peer){
        final var lock = this.getLock(peer).writeLock();
        try {
            lock.lock();
            this.getStatistic(peer).updateFailed();
        } finally {
            lock.unlock();
        }
    }

    void setBitfield(@NonNull Peer peer, @NonNull BitSet bitfield) {
        final var lock = this.getLock(peer).writeLock();
        try {
            lock.lock();
            this.getStatistic(peer).setBitfield(bitfield);
        } finally {
            lock.unlock();
        }
    }

    void updateBitfield(@NonNull Peer peer, int index) {
        final var lock = this.getLock(peer).writeLock();
        try {
            lock.lock();
            this.getStatistic(peer).updateBitfield(index);
        } finally {
            lock.unlock();
        }
    }

    void addPeer(@NonNull Peer peer) {
        final var lock = this.getLock(peer).writeLock();
        try {
            lock.lock();
            this.peers.putIfAbsent(peer, new PeerStatistic(peer));
        } finally {
            lock.unlock();
        }
    }

    private PeerStatisticProjection getStatisticProjection(@NonNull Peer peer) {
        final var lock = this.getLock(peer).readLock();
        try {
            return this.toPeerStatisticProjection(this.getStatistic(peer));
        } finally {
            lock.unlock();
        }
    }

    private PeerStatisticProjection toPeerStatisticProjection(@NonNull PeerStatistic statistic) {
        return PeerStatisticProjection.builder()
                .peer(statistic.getPeer())
                .messageBitfield(statistic
                        .getBitfield()
                        .map(bs -> (BitSet) bs.clone())
                        .orElse(null))
                .failedCount(statistic.getFailedCount())
                .isSeeder(statistic.isSeeder())
                .build();
    }

    private PeerStatistic getStatistic(@NonNull Peer peer) {
        return this.peers.computeIfAbsent(peer, _ -> new PeerStatistic(peer));
    }

    private synchronized ReentrantReadWriteLock getLock(@NonNull Peer peer) {
        return this.locks.computeIfAbsent(peer, _ -> new ReentrantReadWriteLock());
    }
}
