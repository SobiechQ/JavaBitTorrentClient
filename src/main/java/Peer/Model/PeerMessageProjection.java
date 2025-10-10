package Peer.Model;

import Message.Model.MessageProjection;
import lombok.NonNull;

public record PeerMessageProjection(@NonNull Peer peer, @NonNull MessageProjection messageProjection) {}
