package Model.Message;

import Utils.ByteUtils;
import lombok.Getter;

import java.util.Arrays;
import java.util.BitSet;

@Getter
public class MessageBitfield extends MessageProjection {
    private final BitSet bitfield;

    public MessageBitfield(byte[] payload) {
        super(MessageType.BITFIELD, payload);
        this.bitfield = getBitset(payload);
    }

    public boolean hasPiece(int index) {
        return this.bitfield.get(index);
    }

    public boolean isSeeder() {
        return this.bitfield.nextClearBit(0) >= this.bitfield.length();
    }

    private static byte[] getPayload(BitSet bitfield) {
        return bitfield.toByteArray();
    }

    private static BitSet getBitset(byte[] payload) {
        byte[] reversed = new byte[payload.length];
        for (int i = 0; i < payload.length; i++) {
            reversed[i] = ByteUtils.reverseBits(payload[i]);
        }
        return BitSet.valueOf(reversed);
    }

    @Override
    public String toString() {
        return "MessageBitfield{" +
               "length=" + this.getData().length +
               ", bitfield.length=" + bitfield.size() +
               "} ";
    }
}
