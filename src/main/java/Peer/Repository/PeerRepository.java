package Peer.Repository;

import Message.Model.MessageBitfield;
import Peer.Model.Peer;
import Tracker.Model.Messages.TrackerResponse;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Stream;

public class PeerRepository {
    private final Map<Peer, PeerStatistic> peers;

    public PeerRepository() {
        this.peers = new HashMap<>();
    }

    public void addPeers(@NonNull TrackerResponse response) {
        response.getPeers().forEach(this::addPeer);
    }

    public Stream<PeerStatistic> getPeers() {
        return peers.values().stream();
    }

    public Stream<Peer> getUnchokedPeers() {
        return this.getPeers()
                .filter(PeerStatistic::isUnchoked)
                .map(PeerStatistic::getPeer);
    }

    public void setBitfield(@NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.getStatistic(peer).setBitfield(bitfield);
    }

    private PeerStatistic getStatistic(@NonNull Peer peer) {
        return this.peers.computeIfAbsent(peer, _ -> new PeerStatistic(peer));
    }

    private void addPeer(@NonNull Peer peer) {
        this.peers.putIfAbsent(peer, new PeerStatistic(peer));
    }
}
