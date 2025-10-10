package Peer.Service;

import Handshake.Model.HandshakeInputProjection;
import Handshake.Model.HandshakeOutputProjection;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerDataInputProjection;
import Peer.Model.PeerMessageProjection;

import java.util.List;

public interface PeerService {

    void handleInput(Torrent torrent, Peer peer, PeerDataInputProjection projection);
    List<PeerMessageProjection> chokeAlgorithm(Torrent torrent);
    List<PeerMessageProjection> optimisticUnchoke(Torrent torrent);
}
