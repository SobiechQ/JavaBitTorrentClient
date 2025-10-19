package Handshake.Model;

import lombok.NonNull;

import java.util.Arrays;

public record HandshakeInputProjection(byte @NonNull [] handshake) {
    @Override
    public String toString() {
        return "HandshakeInputProjection{" +
               "handshake=" + Arrays.toString(handshake) +
               '}';
    }
}
