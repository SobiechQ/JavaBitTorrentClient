package Bencode;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jooq.lambda.tuple.Tuple2;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

@EqualsAndHashCode
public class Bencode {
    private final BencodeValue bencodeValue;

    public Bencode(@NonNull final String encoded) throws DecodingError {
        this(Bencode.decode(encoded).v1.bencodeValue);
    }
    private Bencode(@NonNull final BencodeValue bencodeValue) {
        this.bencodeValue = bencodeValue;
    }

    public static Optional<Bencode> fromFile(@NonNull final File file) {
        try (final var bis = new BufferedInputStream(new FileInputStream(file))) {
            final var bencode = new Bencode(StandardCharsets.US_ASCII.decode(ByteBuffer.wrap(bis.readAllBytes())).toString());
            return Optional.of(bencode);
        } catch (IOException | DecodingError e) {
            return Optional.empty();
        }
    }


    public static Tuple2<Bencode, String> decode(@NonNull final String encoded) throws DecodingError {
        if (encoded.isEmpty())
            throw new DecodingError("Passed encoded value cant be empty");
        return switch (encoded.charAt(0)){
            case 'i' -> BInt.decode(encoded).map1(bString -> new Bencode(bString));
            case 'l' -> BList.decode(encoded).map1(bString -> new Bencode(bString));
            case 'd' -> BDictionary.decode(encoded).map1(bString -> new Bencode(bString));
            default -> {
                if (Character.isDigit(encoded.charAt(0)))
                    yield BString.decode(encoded).map1(bString -> new Bencode(bString));
                throw new DecodingError("unable to determine type of encoded value");
            }
        };
    }


    public Optional<String> asString() {
        return switch (this.bencodeValue){
            case BString bString -> Optional.of(bString.stringValue);
            default -> Optional.empty();
        };
    }

    public Optional<Long> asInteger() {
        return switch (this.bencodeValue){
            case BInt bInt -> Optional.of(bInt.intValue);
            default -> Optional.empty();
        };
    }

    public Optional<List<Bencode>> asList() {
        return switch (this.bencodeValue){
            case BList bList -> Optional.of(bList.listValue);
            default -> Optional.empty();
        };
    }

    public Optional<Map<String, Bencode>> asDictionary() {
        return switch (this.bencodeValue){
            case BDictionary bDictionary -> Optional.of(bDictionary.dictionaryValue);
            default -> Optional.empty();
        };
    }

    public Optional<Bencode> asDictionary(String key){
        return this.asDictionary()
                .flatMap(map -> Optional.ofNullable(map.get(key)));
    }

    public String encode() {
        return this.getBencodeValue().encode();
    }

    BencodeValue getBencodeValue() {
        return this.bencodeValue;
    }

    @Override
    public String toString() {
        return this.bencodeValue.toString();
    }
}
