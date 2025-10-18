package Peer.Service;

import MessageFactory.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;

public interface PeerService {
    void notifyFailed(Torrent torrent, Peer peer);
    void handleBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
}
