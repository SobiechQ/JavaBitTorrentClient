package Peer.Model;

import MessageFactory.Model.MessageProjection;
import lombok.NonNull;

import java.net.InetSocketAddress;

public record PeerMessageProjection(@NonNull Peer peer, @NonNull MessageProjection messageProjection) {
    public InetSocketAddress getInetSocketAddress() {
        return peer.getInetSocketAddress();
    }
}
