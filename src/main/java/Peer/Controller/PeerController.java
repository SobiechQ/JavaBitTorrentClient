package Peer.Controller;

import Model.Message.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;

import java.util.stream.Stream;

public interface PeerController { //todo remove
    Stream<Peer> getPeers(Torrent torrent);
    Stream<Peer> getPeers(Torrent torrent, int index);
    void handleBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
}
