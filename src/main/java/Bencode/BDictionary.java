package Bencode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode(callSuper = false)
final class BDictionary extends BencodeValue {
    final Map<String, Bencode> dictionaryValue;

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

        final Map<String, Bencode> decoded = new HashMap<>(){
            @Override
            public String toString() {
                return this.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(e -> String.format("%s: {...}", e.getKey()))
                        .collect(Collectors.joining("\n", "{\n", "\n}"));
            }
        };
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

    @Override
    public String encode() {
        final var inner = this.dictionaryValue.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> String.format("%s:%s%s", e.getKey().length(), e.getKey(), e.getValue().encode()))
                .collect(Collectors.joining());

        return String.format("d%se", inner);
    }

    @Override
    public String toString() {
        return this.dictionaryValue.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> String.format("%s: %s", e.getKey(), this.toStringShort(e.getValue().getBencodeValue())))
                .collect(Collectors.joining("\n", "{\n", "\n}"));

    }
    private String toStringShort(BencodeValue bencodeValue) {
        if (bencodeValue instanceof BDictionary){
            return "{...}";
        }
        return bencodeValue.toString();
    }
}
