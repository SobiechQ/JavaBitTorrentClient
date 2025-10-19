package Handlers.Service;

import Handshake.Model.HandshakeInputProjection;
import Model.Message.*;
import Utils.ByteUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

import static Handshake.Service.HandshakeServiceImpl.PROTOCOL_NAME_BYTES;
import static Model.Message.DefaultMessage.*;

@Service
@NoArgsConstructor
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
            buffer.reset();
            return Optional.empty();
        }

        return Optional.of(new HandshakeInputProjection(handshake));
    }

    @Override
    public Optional<MessageProjection> decodeMessage(@NonNull ByteBuffer buffer) {
        buffer.mark();
        if (buffer.remaining() < 4) {
            return Optional.empty();
        }

        final var messageLengthRead = new byte[4];
        buffer.get(messageLengthRead);
        final var messageLength = ByteUtils.bytesToInt(messageLengthRead);

        if (messageLength == 0) {
            log.info("because length, buffer {}", buffer);
            return Optional.of(KEEP_ALIVE.getProjection());
        }

        final var typeByte = buffer.get();
        if (typeByte < 0 || typeByte > 9) {
            buffer.reset();
            return Optional.empty();
        }

        final var messageType = MessageType.valueOf(typeByte);
        return switch (messageType) {
            case CHOKE -> Optional.of(CHOKE.getProjection());
            case UNCHOKE -> Optional.of(UNCHOKE.getProjection());
            case INTERESTED -> Optional.of(INTERESTED.getProjection());
            case NOT_INTERESTED -> Optional.of(NOT_INTERESTED.getProjection());
            case HAVE -> this.decodeMessageHave(buffer, messageLength);
            case BITFIELD -> this.decodeMessageBitfield(buffer, messageLength);
            case REQUEST -> this.decodeMessageRequest(buffer, messageLength);
            case PIECE -> this.decodePiece(buffer, messageLength);
            case KEEP_ALIVE -> Optional.of(KEEP_ALIVE.getProjection());
            case CANCEL -> this.decodeMessageProjection(buffer, MessageType.CANCEL, messageLength);
            case PORT -> this.decodeMessageProjection(buffer, MessageType.PORT, messageLength);
        };
    }

    private Optional<MessageProjection> decodeMessageHave(@NonNull ByteBuffer buffer, int length) {
        if (isTooShort(buffer, length, 4))
            return Optional.empty();

        final var index = this.nextInt(buffer);

        return Optional.of(new MessageHave(index));
    }

    private Optional<MessageProjection> decodeMessageBitfield(@NonNull ByteBuffer buffer, int length) {
        if (isTooShort(buffer, length, length - 1))
            return Optional.empty();

        final var data = new byte[length - 1];
        buffer.get(data);

        return Optional.of(new MessageBitfield(data));
    }

    private Optional<MessageProjection> decodeMessageRequest(@NonNull ByteBuffer buffer, int length) {
        if (isTooShort(buffer, length, 12))
            return Optional.empty();

        final var index = this.nextInt(buffer);
        final var begin = this.nextInt(buffer);
        final var requestLength = this.nextInt(buffer);

        return Optional.of(new MessageRequest(index, begin, requestLength));
    }

    private Optional<MessageProjection> decodePiece(@NonNull ByteBuffer buffer, int length) {
        if (isTooShort(buffer, length, length - 1))
            return Optional.empty();

        final var index = this.nextInt(buffer);
        final var begin = this.nextInt(buffer);

        final var piece = new byte[length - 9];
        buffer.get(piece);

        return Optional.of(new MessagePiece(index, begin, piece));
    }

    private Optional<MessageProjection> decodeMessageProjection(@NonNull ByteBuffer buffer, @NonNull MessageType messageType, int length) {
        if (isTooShort(buffer, length, length - 1))
            return Optional.empty();

        final var payload = new byte[length - 1];
        buffer.get(payload);

        return Optional.of(new MessageProjection(messageType, payload));
    }

    private int nextInt(@NonNull ByteBuffer buffer) {
        final var readArray = new byte[4];
        buffer.get(readArray);
        return ByteUtils.bytesToInt(readArray);
    }

    private boolean isTooShort(@NonNull ByteBuffer buffer, int decodedLength, int minimalLength) {
        final var isTooShort = (decodedLength - 1) < minimalLength || buffer.remaining() < minimalLength;
        if (isTooShort)
            buffer.reset();
        return isTooShort;
    }
}
