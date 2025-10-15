package Peer.Service;

import MessageFactory.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;

public interface PeerService {

    void announce(Torrent torrent);
    void subscribeAsyncRevalidation(Torrent torrent);
    void notifyAttempt(Torrent torrent, Peer peer);
    void notifySuccess(Torrent torrent, Peer peer);
    void handleBitfield(Torrent torrent, Peer peer, MessageBitfield bitfield);
    void handleChoke(Torrent torrent, Peer peer);
    void handleUnchoke(Torrent torrent, Peer peer);
}
