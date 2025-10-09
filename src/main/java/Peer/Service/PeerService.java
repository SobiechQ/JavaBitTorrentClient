package Peer.Service;

import Model.DecodedBencode.Torrent;
import Peer.Model.PeerMessage;

import java.util.List;

public interface PeerService {

    List<PeerMessage> chokeAlgorithm(Torrent torrent);
    List<PeerMessage> optimisticUnchoke(Torrent torrent);
}
