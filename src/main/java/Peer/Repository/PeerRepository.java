package Peer.Repository;

import MessageFactory.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;

import java.util.stream.Stream;

public interface PeerRepository {
    void addPeer(Torrent torrent, Peer peer);
    void updateAttempt(Torrent torrent, Peer peer);
    void updateLastSeen(Torrent torrent, Peer peer);
    void setBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
    Stream<PeerStatistic> getPeerStatisticProjection(Torrent torrent);
    void setChoked(Torrent torrent, Peer peer);
    void setUnchoked(Torrent torrent, Peer peer);

}
