package Peer.Repository;

import Model.Message.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerStatisticProjection;

import java.util.stream.Stream;

public interface PeerRepository {
    void addPeer(Torrent torrent, Peer peer);
    void updateFailed(Torrent torrent, Peer peer);
    void setBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
    Stream<PeerStatisticProjection> getPeerStatisticProjection(Torrent torrent);
}
