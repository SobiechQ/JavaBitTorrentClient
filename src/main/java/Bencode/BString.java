package Bencode;

import lombok.Getter;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import java.util.function.Function;

@Getter
final class BString extends BencodeValue {
    final String stringValue;

    private BString(@NonNull String stringValue){
        this.stringValue = stringValue;
    }

    public static Tuple2<BString, String> decode(@NonNull final String encoded) throws DecodingError {
        if (encoded.length() < 2 || !Character.isDigit(encoded.charAt(0)))
            throw new DecodingError("encoded value does not represent string");

        final var lengthRead =
                Seq.ofType(encoded.chars().boxed(), Integer.class)
                        .map(c -> (char) c.intValue())
                        .limitUntil(c -> c == ':')
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();

        final long length;
        try {
            length = Long.parseLong(lengthRead);
        } catch (NumberFormatException e) {
            throw new DecodingError(e);
        }

        final Function<Seq<Character>, String> joining = (seq) -> seq
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();

        final var split = Seq.ofType(encoded.chars().boxed(), Integer.class)
                .map(c -> (char) c.intValue())
                .skipUntilClosed(c -> c == ':')
                .splitAt(length)
                .map1(joining)
                .map2(joining);

        if (split.v1.length() < length)
            throw new DecodingError("String was shorter then its declared length");

        return split.map1(s -> new BString(s));
    }

    @Override
    public String encode() {
        return String.format("%s:%s", this.stringValue.length(), this.stringValue);
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", this.stringValue);
    }
}
