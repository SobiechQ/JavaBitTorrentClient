package Handlers.Service;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;

import java.nio.ByteBuffer;

public interface HandlerService {
    void handleMessageInput(Torrent torrent, Peer peer, ByteBuffer buffer, int length);
}
