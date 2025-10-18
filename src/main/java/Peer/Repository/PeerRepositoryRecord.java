package Peer.Repository;

import MessageFactory.Model.MessageBitfield;
import Peer.Model.Peer;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Stream;


class PeerRepositoryRecord {
    private final Map<Peer, PeerStatistic> peers;

    PeerRepositoryRecord() {
        this.peers = new HashMap<>();
    }

    Stream<PeerStatistic> getPeers() {
        return peers.values().stream();
    }

    void updateFailed(@NonNull Peer peer){
        this.getStatistic(peer).updateFailed();
    }

    void setBitfield(@NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.getStatistic(peer).setBitfield(bitfield);
    }

    PeerStatistic getStatistic(@NonNull Peer peer) {
        return this.peers.computeIfAbsent(peer, _ -> new PeerStatistic(peer));
    }

    void addPeer(@NonNull Peer peer) {
        this.peers.putIfAbsent(peer, new PeerStatistic(peer));
    }
}
