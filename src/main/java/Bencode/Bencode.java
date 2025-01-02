package Bencode;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;

import java.util.*;
import java.util.function.Function;

@EqualsAndHashCode
public class Bencode {

    @Nullable
    private final String stringValue;
    @Nullable
    private final Long intValue;
    @Nullable
    private final List<Bencode> listValue;
    @Nullable
    private final Map<String, Bencode> dictionaryValue;

    public Bencode(@NonNull final String encoded) throws DecodingError {

        this.stringValue = Bencode.decodeString(encoded)
                .map(Tuple2::v1)
                .orElse(null);

        this.intValue = Bencode.decodeInteger(encoded)
                .map(Tuple2::v1)
                .orElse(null);

        this.listValue = Bencode.decodeList(encoded)
                .map(Tuple2::v1)
                .orElse(null);

        this.dictionaryValue = Bencode.decodeDictionary(encoded)
                .map(Tuple2::v1)
                .orElse(null);

    }
    public static Optional<Tuple2<Bencode, String>> decode(@NonNull final String string) throws DecodingError {
        final var stringValue = Bencode.decodeString(string)
                .map(t -> t.map1(_ -> new Bencode(string)));
        if (stringValue.isPresent())
            return stringValue;

        final var integerValue = Bencode.decodeInteger(string)
                .map(t -> t.map1(_ -> new Bencode(string)));
        if (integerValue.isPresent())
            return integerValue;

        final var listValue = Bencode.decodeList(string)
                .map(t -> t.map1(_ -> new Bencode(string)));
        if (listValue.isPresent())
            return listValue;

        return Bencode.decodeDictionary(string)
                .map(t -> t.map1(_ -> new Bencode(string)));
    }

    public static Optional<Tuple2<String, String>> decodeString(@NonNull final String encoded) throws DecodingError {
        if (encoded.isEmpty() || encoded.charAt(0) < '0' || encoded.charAt(0) > '9')
            return Optional.empty();

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

        return Optional.of(split);
    }

    public static Optional<Tuple2<Long, String>> decodeInteger(@NonNull final String encoded) throws DecodingError {
        if (encoded.isEmpty() || encoded.charAt(0) != 'i')
            return Optional.empty();

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
            return Optional.of(new Tuple2<>(Long.parseLong(decodedRead), decodedRemaining));
        } catch (NumberFormatException e) {
            throw new DecodingError(e);
        }
    }

    public static Optional<Tuple2<List<Bencode>, String>> decodeList(@NonNull final String encoded) throws DecodingError {
        if (encoded.isEmpty() || encoded.charAt(0) != 'l') //If it's not a list, return empty
            return Optional.empty();

        final var decodedRemaining = Seq.ofType(encoded.chars().boxed(), Integer.class)
                .skip(1)
                .map(c -> (char) c.intValue())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        final List<Bencode> decoded = new LinkedList<>();
        do {
            Bencode.decode(decodedRemaining.toString())
                    .ifPresent(pair -> {
                        decoded.add(pair.v1);
                        decodedRemaining.setLength(0);
                        decodedRemaining.append(pair.v2);
                    });
        } while (!decodedRemaining.isEmpty() && decodedRemaining.charAt(0) != 'e');
        decodedRemaining.deleteCharAt(0);


        return Optional.of(new Tuple2<>(decoded, decodedRemaining.toString()));
    }
    public static Optional<Tuple2<Map<String, Bencode>, String>> decodeDictionary(@NonNull final String encoded) throws DecodingError {
        if (encoded.isEmpty() || encoded.charAt(0) != 'd') //If it's not a list, return empty
            return Optional.empty();

        final var decodedRemaining = Seq.ofType(encoded.chars().boxed(), Integer.class)
                .skip(1)
                .map(c -> (char) c.intValue())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        final Map<String, Bencode> decoded = new HashMap<>();
        do {
            final var keyAndRemaining = Bencode.decodeString(decodedRemaining.toString());
            final var valueAndRemaining = keyAndRemaining.flatMap(tuple -> Bencode.decode(tuple.v2));

            if (keyAndRemaining.isEmpty())
                break;

            final var key = keyAndRemaining
                    .map(Tuple2::v1)
                    .orElseThrow(()->new DecodingError("Unable to parse Key"));
            final var value = valueAndRemaining
                    .map(Tuple2::v1)
                    .orElseThrow(()->new DecodingError("Unable to parse value"));

            decoded.putIfAbsent(key,value);

            valueAndRemaining
                    .map(Tuple2::v2)
                    .ifPresent(remaining -> {
                decodedRemaining.setLength(0);
                decodedRemaining.append(remaining);
            });

        } while (!decodedRemaining.isEmpty() && decodedRemaining.charAt(0) != 'e');
        decodedRemaining.deleteCharAt(0);

        return Optional.of(new Tuple2<>(Collections.unmodifiableMap(decoded), decodedRemaining.toString()));
    }

    public Optional<String> asString() {
        return Optional.ofNullable(this.stringValue);
    }

    public Optional<Long> asInteger() {
        return Optional.ofNullable(this.intValue);
    }

    public Optional<List<Bencode>> asList() {
        return Optional.ofNullable(this.listValue);
    }

    public Optional<Map<String, Bencode>> asDictionary() {
        return Optional.ofNullable(this.dictionaryValue);
    }

}
