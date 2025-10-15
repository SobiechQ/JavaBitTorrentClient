package MessageFactory.Model;

import java.util.Map;
import java.util.Optional;

public class MessageBitfield extends MessageProjection {
    private final Map<Integer, Boolean> bitfield;

    public MessageBitfield(byte[] payload, Map<Integer, Boolean> bitfield) {
        super(MessageType.BITFIELD, payload);
        this.bitfield = bitfield;
    }

    public boolean hasPiece(int index) {
        return Optional.ofNullable(bitfield.get(index)).orElse(false);
    }

    public boolean isSeeder() {
        return this.bitfield.values()
                .stream()
                .allMatch(t -> t);
    }
}
