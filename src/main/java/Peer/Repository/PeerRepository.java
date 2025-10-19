package Peer.Repository;

import Model.Message.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerStatisticProjection;

import java.util.BitSet;
import java.util.stream.Stream;

public interface PeerRepository {
    void addPeer(Torrent torrent, Peer peer);
    void updateFailed(Torrent torrent, Peer peer);
    void setBitfield(Torrent torrent, Peer peer, BitSet bitfield);
    void updateBitfield(Torrent torrent, Peer peer, int index);
    Stream<PeerStatisticProjection> getPeerStatisticProjection(Torrent torrent);
}
