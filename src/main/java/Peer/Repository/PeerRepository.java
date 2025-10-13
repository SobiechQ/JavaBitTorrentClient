package Peer.Repository;

import Message.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;

public interface PeerRepository {
    void addPeer(Torrent torrent, Peer peer);
    void updateLastSeen(Torrent torrent, Peer peer);
    void setBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
}
