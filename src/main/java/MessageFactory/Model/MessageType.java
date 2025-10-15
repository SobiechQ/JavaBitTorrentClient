package MessageFactory.Model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MessageType {
    KEEP_ALIVE((byte) -1), CHOKE((byte) 0), UNCHOKE((byte) 1), INTERESTED((byte) 2), NOT_INTERESTED((byte) 3), HAVE((byte) 4), BITFIELD((byte) 5), REQUEST((byte) 6), PIECE((byte) 7), CANCEL((byte) 8);
    private final byte value;

    MessageType(byte value) {
        this.value = value;
    }

    public static MessageType valueOf(byte value) {
        return Arrays.stream(MessageType.values())
                .filter(type -> type.value == value)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No message type with byte value: " + value));
    }


}
/*
0 - choke
1 - unchoke
2 - interested
3 - not interested
4 - have
5 - bitfield
6 - request
7 - piece
8 - cancel
 */