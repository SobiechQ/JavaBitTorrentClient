package Peer.Model;

import Model.Message.MessageProjection;
import lombok.NonNull;

import java.net.InetSocketAddress;

public record PeerMessageProjection(@NonNull Peer peer, @NonNull MessageProjection messageProjection) {
    public InetSocketAddress getInetSocketAddress() {
        return peer.getInetSocketAddress();
    }
}
