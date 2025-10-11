package Peer.Service;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerMessageProjection;

import java.util.List;
import java.util.stream.Stream;

public interface PeerService {

    Stream<Peer> getPeers(Torrent torrent);
    Stream<Peer> getPeers(Torrent torrent, int index);
    void notifySuccess(Torrent torrent, Peer peer);
    void handleBitfield(Torrent torrent, Peer peer);
    List<PeerMessageProjection> chokeAlgorithm(Torrent torrent);
    List<PeerMessageProjection> optimisticUnchoke(Torrent torrent);
}
