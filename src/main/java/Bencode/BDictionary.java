package Bencode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class BDictionary extends BencodeValue {

    final Map<String, Bencode> dictionaryValue;

    public BDictionary(@NonNull String encoded) {
        this.dictionaryValue = BDictionary.decode(encoded).v1.getDictionaryValue();
    }
    private BDictionary(Map<String, Bencode> dictionaryValue) {
        this.dictionaryValue = dictionaryValue;
    }

    public static Tuple2<BDictionary, String> decode(@NonNull final String encoded) throws DecodingError {
        if (encoded.length() < 2 || encoded.charAt(0) != 'd')
            throw new DecodingError("Encoded value does not represent dictionary");

        final var remaining = Seq.ofType(encoded.chars().boxed(), Integer.class)
                .skip(1)
                .map(c -> (char) c.intValue())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        final Map<String, Bencode> decoded = new HashMap<>();
        while (!remaining.isEmpty() && remaining.charAt(0) != 'e'){


            final var keyAndRemaining = BString.decode(remaining.toString());
            final var valueAndRemaining = Bencode.decode(keyAndRemaining.v2);

            decoded.putIfAbsent(keyAndRemaining.v1.getStringValue(),valueAndRemaining.v1);

            remaining.setLength(0);
            remaining.append(valueAndRemaining.v2);

        }
        remaining.deleteCharAt(0);

        return new Tuple2<>(Collections.unmodifiableMap(decoded), remaining.toString())
                .map1(map -> new BDictionary(map));
    }
}
