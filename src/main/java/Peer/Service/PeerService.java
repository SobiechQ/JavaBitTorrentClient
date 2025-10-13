package Peer.Service;

import Message.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerMessageProjection;

import java.util.List;
import java.util.stream.Stream;

public interface PeerService {

    void announce(Torrent torrent);
    void subscribeAsyncRevalidation(Torrent torrent);
    void notifySuccess(Torrent torrent, Peer peer);
    void handleBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
}
