package Peer.Service;

import Model.Message.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Model.Message.MessageHave;
import Peer.Model.Peer;

public interface PeerService {
    void notifyFailed(Torrent torrent, Peer peer);
    void handleBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
    void handleHave(Torrent torrent, Peer peer, MessageHave have);
}
