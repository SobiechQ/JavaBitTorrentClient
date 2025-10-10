package Message.Service;

import Message.Model.*;
import Peer.Model.PeerDataInputProjection;
import Utils.ByteUtils;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static Message.Model.DefaultMessage.*;
import static Utils.ByteUtils.bytesToInt;

@Service
public class MessageServiceImpl implements MessageService {
    private final static int REQUEST_LENGTH = (int) Math.pow(2, 14);

    @Override
    public int getLength(byte[] lengthPrefix) {
        if (lengthPrefix.length != 4)
            throw new IllegalArgumentException("Length prefix must be 4 bytes");
        return ByteUtils.bytesToInt(lengthPrefix);
    }

    @Override
    public MessageProjection decode(PeerDataInputProjection inputProjection) {
        final var data = inputProjection.data();

        if (data.length == 0)
            return KEEP_ALIVE.getProjection();

        final var receivedMessageType = MessageType.valueOf(data[0]);

        switch (receivedMessageType) {
            case PIECE -> {
                return piece(inputProjection);
            }
            case BITFIELD -> {
                return bitfield(inputProjection);
            }
        }

        final var receivedPayload = new byte[data.length - 1];
        System.arraycopy(data, 1, receivedPayload, 0, receivedPayload.length);

        return new MessageProjection(receivedMessageType, receivedPayload);
    }

    @Override
    public MessageHave have(int index) {
        return new MessageHave(index);
    }

    @Override
    public MessageRequest request(int index, int begin) {
        return new MessageRequest(index, begin, REQUEST_LENGTH);
    }

    @Override
    public MessagePiece piece(PeerDataInputProjection inputProjection) {
        final var data = inputProjection.data();

        final var receivedIndex = new byte[4];
        System.arraycopy(data, 1, receivedIndex, 0, 4);

        final var receivedBegin = new byte[4];
        System.arraycopy(data, 5, receivedBegin, 0, 4);

        final var receivedPiece = new byte[data.length - 9];
        System.arraycopy(data, 9, receivedPiece, 0, data.length - 9);

        return new MessagePiece(bytesToInt(receivedIndex), bytesToInt(receivedBegin), receivedPiece);
    }

    @Override
    public MessageBitfield bitfield(PeerDataInputProjection inputProjection) {
        final var data = inputProjection.data();

        final var payload = new byte[data.length - 1];
        System.arraycopy(data, 1, payload, 0, payload.length);

        final var bitfield = Seq.ofType(ByteUtils.bytesToStream(payload), Byte.class)
                .map(ByteUtils::byteToBits)
                .flatMap(ByteUtils::bitsToStream)
                .zipWithIndex()
                .map(t -> t.map2(Math::toIntExact))
                .collect(() -> new HashMap<Integer, Boolean>(),
                        (map, tuple) -> map.putIfAbsent(tuple.v2, tuple.v1),
                        Map::putAll);

        return new MessageBitfield(payload, bitfield);
    }
}
