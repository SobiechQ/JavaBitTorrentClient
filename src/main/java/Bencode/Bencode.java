package Bencode;

import com.google.common.base.Charsets;
import io.vavr.control.Try;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jooq.lambda.tuple.Tuple2;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@EqualsAndHashCode
public class Bencode {
    private final BencodeValue bencodeValue;

    public Bencode(final byte @NonNull [] bytes) {
        this.bencodeValue = new Bencode(Bencode.byteArrayToString(bytes)).bencodeValue;
    }

    public Bencode(@NonNull final String encoded) throws DecodingError {
        this(Bencode.decode(encoded).v1.bencodeValue);
    }
    private Bencode(@NonNull final BencodeValue bencodeValue) {
        this.bencodeValue = bencodeValue;
    }

    public static Optional<Bencode> fromFile(@NonNull final File file) { //todo unsafe!!
        final var bytes = Try.of(() -> new BufferedInputStream(new FileInputStream(file)))
                .mapTry(InputStream::readAllBytes)
                .toJavaStream();

        final var arr = bytes.flatMapToInt(b ->
                        IntStream.range(0, b.length).map(i -> b[i] & 0xFF))
                .toArray();

        final var str = new String(arr, 0, arr.length);

        return Optional.of(new Bencode(str));
    }

    public static String byteArrayToString(final byte @NonNull [] bytes) {
       return new String(bytes, Charsets.ISO_8859_1);
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
            case BString bString -> Optional.of(bString.getStringValue());
            default -> Optional.empty();
        };
    }

    public Optional<Long> asInteger() {
        return switch (this.bencodeValue){
            case BInt bInt -> Optional.of(bInt.getIntValue());
            default -> Optional.empty();
        };
    }

    public Optional<List<Bencode>> asList() {
        return switch (this.bencodeValue){
            case BList bList -> Optional.of(bList.getListValue());
            default -> Optional.empty();
        };
    }

    public Optional<Map<String, Bencode>> asDictionary() {
        return switch (this.bencodeValue){
            case BDictionary bDictionary -> Optional.of(bDictionary.getDictionaryValue());
            default -> Optional.empty();
        };
    }

    public Optional<Bencode> asDictionary(String key){
        return this.asDictionary()
                .flatMap(map -> Optional.ofNullable(map.get(key)));
    }

    public Stream<Bencode> stream(){
        return switch (this.bencodeValue){
            case BString _, BInt _ -> Stream.of(this);
            case BList bList -> bList.getListValue().stream();
            case BDictionary bDictionary -> bDictionary.getDictionaryValue().values().stream();
        };
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
