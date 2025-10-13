package Peer.Service;

import Peer.Model.Peer;

import java.util.stream.Stream;

public interface PeerStrategyService {

    Stream<Peer> getPeers();
}
