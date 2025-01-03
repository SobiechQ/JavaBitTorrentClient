package Bencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class BencodeIntegerTest {


    static Stream<Arguments> sourceTestDecodeInteger() throws DecodingError {
        return Stream.of(
                Arguments.of("i0e", 0, ""),
                Arguments.of("i10ei10e", 10, "i10e"),
                Arguments.of("i123456ee", 123456, "e"),
                Arguments.of("i-10ei", -10, "i"),
                Arguments.of("i-42e4242", -42, "4242"),
                Arguments.of("i-0e", 0, "")
        );
    }
    @ParameterizedTest
    @MethodSource("sourceTestDecodeInteger")
    void testDecodeInteger(String encoded, long decoded, String remaining) throws DecodingError{
        final var tuple = Bencode.decode(encoded);
        Assertions.assertEquals(remaining, tuple.v2);

        Assertions.assertTrue(tuple.v1.asInteger().isPresent());

        final var bencode = new Bencode(encoded);

        Assertions.assertTrue(bencode.asInteger().isPresent());
        Assertions.assertFalse(bencode.asList().isPresent());
        Assertions.assertFalse(bencode.asString().isPresent());
        Assertions.assertFalse(bencode.asDictionary().isPresent());
        Assertions.assertEquals(decoded, bencode.asInteger().get());
    }

    static Stream<Arguments> sourceTestDecodeIntegerThrows() throws DecodingError {
        return Stream.of(
                Arguments.of("i10"),
                Arguments.of("i"),
                Arguments.of("10e"),
                Arguments.of("-10e"),
                Arguments.of("iie"),
                Arguments.of("e"),
                Arguments.of(""),
                Arguments.of(":"),
                Arguments.of("ie"),
                Arguments.of("iae"),
                Arguments.of("i21.37e"),
                Arguments.of("i21,37e")
        );
    }
    @ParameterizedTest
    @MethodSource("sourceTestDecodeIntegerThrows")
    void testDecodeIntegerThrows(String encoded) {
        Assertions.assertThrows(DecodingError.class, () -> BList.decode(encoded));
    }

}