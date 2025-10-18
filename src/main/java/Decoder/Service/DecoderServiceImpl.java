package Decoder.Service;

import Handshake.Model.HandshakeInputProjection;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;
import static Handshake.Service.HandshakeServiceImpl.PROTOCOL_NAME_BYTES;

@Service
public class DecoderServiceImpl implements DecoderService {

    private static final Logger log = LoggerFactory.getLogger(DecoderServiceImpl.class);

    @Override
    public Optional<HandshakeInputProjection> decodeHandshake(@NonNull ByteBuffer buffer) {
        buffer.rewind();
        if (buffer.remaining() < 68) {
            return Optional.empty();
        }

        buffer.mark();

        if (buffer.get() != 19) {
            buffer.reset();
            return Optional.empty();
        }

        final var handshake = new byte[68];
        buffer.reset();
        buffer.get(handshake);

        byte[] protocolNameBytes = new byte[19];
        System.arraycopy(handshake, 1, protocolNameBytes, 0, 19);

        if (!Arrays.equals(protocolNameBytes, PROTOCOL_NAME_BYTES)) {
            buffer.position(buffer.position() - 68);
            return Optional.empty();
        }

        return Optional.of(new HandshakeInputProjection(handshake));
    }
}
