package Peer.Model;

import Message.Model.MessageProjection;
import lombok.NonNull;

public record PeerMessage (@NonNull Peer peer, @NonNull MessageProjection messageProjection) {}
