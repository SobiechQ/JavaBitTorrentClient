package Peer.Service;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerMessageProjection;

import java.util.stream.Stream;

public interface PeerStrategyService {
    Stream<Peer> getPeers(Torrent torrent);
    Stream<Peer> getPeers(Torrent torrent, int index);
    Stream<PeerMessageProjection> chokingAndUnchoking(Torrent torrent);
    Stream<PeerMessageProjection> optimisticUnchoke(Torrent torrent);
    Stream<PeerMessageProjection> chokeUnreachable(Torrent torrent);
}
