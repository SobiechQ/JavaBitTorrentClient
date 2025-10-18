package Peer.Controller;

import MessageFactory.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerMessageProjection;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface PeerController {
    Stream<Peer> getPeers(Torrent torrent);
    Stream<Peer> getPeers(Torrent torrent, int index);
    void handleBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
}
