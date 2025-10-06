package Model.Bencode;

import com.google.common.base.Charsets;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jooq.lambda.tuple.Tuple2;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Used for encoded and decoded bencode values.
 * Every Bencode object contains a {@link BencodeValue bencode value} that represents one of:
 * <ul>
 *     <li>{@link BInt Encoded Int}</li>
 *     <li>{@link BInt Encoded String}</li>
 *     <li>{@link BInt Encoded List}</li>
 *     <li>{@link BInt Encoded Dictionary}</li>
 * </ul>
 * Decoding is typesafe and values are returned as Optional of requested type: {@link Bencode#asInteger() Int}, {@link Bencode#asInteger() String}, {@link Bencode#asInteger() List}, {@link Bencode#asInteger() Dict}. <br>
 * Bencode object can be created by invoking constructors or by static method {@link Bencode#fromFile(File)}.
 */
@EqualsAndHashCode
public class Bencode {
    private final BencodeValue bencodeValue;
    public final static Charset CHARSET = Charsets.ISO_8859_1;

    /**
     * @param bytes Bytes of bencode encoded message.
     */
    public Bencode(final byte @NonNull [] bytes) {
        this.bencodeValue = new Bencode(Bencode.byteArrayToString(bytes)).bencodeValue;
    }

    /**
     * @param encoded Bencode encoded string.
     * @throws DecodingError if parser was unable to decode string.
     */
    public Bencode(@NonNull final String encoded) throws DecodingError {
        this(Bencode.decode(encoded).v1.bencodeValue);
    }

    private Bencode(@NonNull final BencodeValue bencodeValue) {
        this.bencodeValue = bencodeValue;
    }

    /**
     * Reads all encoded bytes from file, decodes them, and creates bencode object.
     * @param file file to read from. Reads all bytes from file.
     * @return Decoded Bencode object
     * @throws IOException If IO operations fail.
     * @throws DecodingError If file was fully read, but parser was unable to decode bytes.
     */
    public static Bencode fromFile(@NonNull final File file) throws IOException, DecodingError {
        final Stream<byte[]> bytes;

        try (final var bis = new BufferedInputStream(new FileInputStream(file))){
            bytes = Stream.of(bis.readAllBytes());
        }

        final var arr = bytes.flatMapToInt(b ->
                        IntStream.range(0, b.length).map(i -> b[i] & 0xFF))
                .toArray();

        final var str = new String(arr, 0, arr.length);

        return new Bencode(str);
    }

    private static String byteArrayToString(final byte @NonNull [] bytes) {
       return new String(bytes, Bencode.CHARSET);
    }

    public static Tuple2<Bencode, String> decode(@NonNull final String encoded) throws DecodingError {
        if (encoded.isEmpty())
            throw new DecodingError("Passed encoded value cant be empty");
        return switch (encoded.charAt(0)){
            case 'i' -> BInt.decode(encoded).map1(Bencode::new);
            case 'l' -> BList.decode(encoded).map1(Bencode::new);
            case 'd' -> BDictionary.decode(encoded).map1(Bencode::new);
            default -> {
                if (Character.isDigit(encoded.charAt(0)))
                    yield BString.decode(encoded).map1(Bencode::new);
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
