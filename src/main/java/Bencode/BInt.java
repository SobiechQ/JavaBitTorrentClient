package Bencode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Optional;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class BInt extends BencodeValue {

    final long intValue;

    public BInt(@NonNull String encoded) {
        this.intValue = BInt.decode(encoded).v1.getIntValue();
    }

    private BInt(long intValue) {
        this.intValue = intValue;
    }

    public static Tuple2<BInt, String> decode(@NonNull final String encoded) throws DecodingError {
        if (encoded.isEmpty() || encoded.charAt(0) != 'i')
            throw new DecodingError("Encoded value does not represent integer");

        if (!Seq.ofType(encoded.chars().boxed(), Integer.class)
                .map(c -> (char) c.intValue())
                .skip(1)
                .containsAny('e')
        ) throw new DecodingError("Encoded integers must contain end character");

        final var decodedRead = Seq.ofType(encoded.chars().boxed(), Integer.class)
                .map(c -> (char) c.intValue())
                .skip(1)
                .limitUntil(c -> c == 'e')
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();

        final var decodedRemaining = Seq.ofType(encoded.chars().boxed(), Integer.class)
                .map(c -> (char) c.intValue())
                .skipUntilClosed(c -> c == 'e')
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();

        try {
            return new Tuple2<>(Long.parseLong(decodedRead), decodedRemaining)
                    .map1(l -> new BInt(l));
        } catch (NumberFormatException e) {
            throw new DecodingError(e);
        }
    }
}