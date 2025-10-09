package Handshake.Model;

import lombok.NonNull;

public record HandshakeInputProjection(byte @NonNull [] handshake) {
}
