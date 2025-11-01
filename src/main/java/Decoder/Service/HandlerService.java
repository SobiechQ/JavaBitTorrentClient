package Decoder.Service;

import Model.DecodedBencode.Torrent;
import Model.Message.MessageProjection;
import Peer.Model.Peer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Consumer;

public interface HandlerService {
    void handleMessageInput(Torrent torrent, Peer peer, ByteBuffer buffer, int length, Consumer<MessageProjection> responseSender);
    boolean isMessageComplete(ByteBuffer buffer, int length);
}
