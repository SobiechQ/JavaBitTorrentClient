package MessageFactory.Model;

import Utils.ByteUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageBitfieldTest {

    @Test
    void testBitfield() {
        final var encoded = new byte[] {(byte) 0xC9, (byte) 0x80};
        final var bitfield = new MessageBitfield(encoded);

        final var booleans = ByteUtils.bytesToBooleans(encoded);
        for (int i = 0; i < 16; i++) {
            Assertions.assertEquals(booleans[i], bitfield.hasPiece(i));
        }
    }

}