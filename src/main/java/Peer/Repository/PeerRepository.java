package Peer.Repository;

import Peer.Model.Peer;
import Peer.Model.PeerStatistic;
import Tracker.Model.Messages.TrackerResponse;
import lombok.Getter;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.util.*;
import java.util.stream.Stream;

public class PeerRepository {
    private final Map<Peer, PeerStatistic> peers;

    public PeerRepository() {
        this.peers = new HashMap<>();
    }

    public void addPeers(TrackerResponse response) {
        response.getPeers().forEach(this::addPeer);
    }

    private void addPeer(Peer peer) {
        this.peers.putIfAbsent(peer, new PeerStatistic(peer));
    }

    public Seq<PeerStatistic> getPeers() {
        return Seq.ofType(peers.values().stream(), PeerStatistic.class);
    }
}
