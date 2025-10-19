package Decoder.Service;

import Handshake.Model.HandshakeInputProjection;
import Model.Message.MessageProjection;

import java.nio.ByteBuffer;
import java.util.Optional;

public interface DecoderService {
    Optional<HandshakeInputProjection> decodeHandshake(ByteBuffer buffer);
    Optional<MessageProjection> decodeMessage(ByteBuffer buffer);
}
