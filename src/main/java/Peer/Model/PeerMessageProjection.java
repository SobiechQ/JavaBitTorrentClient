package Peer.Model;

import MessageFactory.Model.MessageProjection;
import lombok.NonNull;

public record PeerMessageProjection(@NonNull Peer peer, @NonNull MessageProjection messageProjection) {}
