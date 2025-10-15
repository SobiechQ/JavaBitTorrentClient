package MessageFactory.Model;

import Utils.ByteUtils;
import lombok.Getter;

import static MessageFactory.Model.MessageType.PIECE;

@Getter
public class MessagePiece extends MessageProjection {
    private final int index;
    private final int begin;
    private final byte[] piece;

    public MessagePiece(int index, int begin, byte[] piece) {
        super(PIECE, toPayload(index, begin, piece));
        this.index = index;
        this.begin = begin;
        this.piece = piece;
    }

    private static byte[] toPayload(int index, int begin, byte[] piece) {
        final var payload = new byte[piece.length + 8];
        System.arraycopy(ByteUtils.intToBytes(index), 0, payload, 0, 4);
        System.arraycopy(ByteUtils.intToBytes(begin), 0, payload, 4, 4);
        System.arraycopy(piece, 0, payload, 8, piece.length);
        return payload;
    }
}
