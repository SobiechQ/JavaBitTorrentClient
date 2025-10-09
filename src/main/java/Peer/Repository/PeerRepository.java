package Peer.Repository;

import Message.Model.MessageBitfield;
import Peer.Model.Peer;
import Tracker.Model.Messages.TrackerResponse;
import lombok.NonNull;
import org.jooq.lambda.Seq;

import java.util.*;

public class PeerRepository {
    private final Map<Peer, PeerStatistic> peers;

    public PeerRepository() {
        this.peers = new HashMap<>();
    }

    public void addPeers(@NonNull TrackerResponse response) {
        response.getPeers().forEach(this::addPeer);
    }

    public Seq<PeerStatistic> getPeers() {
        return Seq.ofType(peers.values().stream(), PeerStatistic.class);
    }

    public boolean hasBitfield(@NonNull Peer peer, int index){
        return this.getStatistic(peer)
                .getBitfield()
                .map(m -> m.hasPiece(index))
                .orElse(false);
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
