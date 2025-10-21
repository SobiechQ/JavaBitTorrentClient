package Handlers.Service;

import Model.DecodedBencode.Torrent;
import Model.Message.MessageProjection;
import Peer.Model.Peer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

public interface HandlerService {
    List<MessageProjection> handleMessageInput(Torrent torrent, Peer peer, ByteBuffer buffer, int length);
    boolean isMessageComplete(ByteBuffer buffer, int length);
}
