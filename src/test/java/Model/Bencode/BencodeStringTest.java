package Model.Bencode;

import Model.Bencode.BString;
import Model.Bencode.Bencode;
import Model.Bencode.DecodingError;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;


class BencodeStringTest {


    static Stream<Arguments> sourceTestDecodeString() {
        return Stream.of(
                Arguments.of("6:pjwstk", "pjwstk", ""),
                Arguments.of("7:abcdefg", "abcdefg", ""),
                Arguments.of("1:a", "a", ""),
                Arguments.of("2:abcd", "ab", "cd"),
                Arguments.of("3:ab:cd", "ab:", "cd"),
                Arguments.of("4::::::::::", "::::", ":::::"),
                Arguments.of("0:", "", ""),
                Arguments.of("0:::", "", "::"),
                Arguments.of("0:abcd", "", "abcd")
        );
    }
    @ParameterizedTest
    @MethodSource("sourceTestDecodeString")
    void testDecodeString(String encoded, String decoded, String remaining) {
        final var tuple = Bencode.decode(encoded);
        Assertions.assertEquals(remaining, tuple.v2);

        Assertions.assertTrue(tuple.v1.asString().isPresent());

        final var bencode = new Bencode(encoded);

        Assertions.assertTrue(bencode.asString().isPresent());
        Assertions.assertFalse(bencode.asList().isPresent());
        Assertions.assertFalse(bencode.asInteger().isPresent());
        Assertions.assertFalse(bencode.asDictionary().isPresent());
        Assertions.assertEquals(decoded, bencode.asString().get());
    }

    static Stream<Arguments> sourceTestDecodeStringThrows(){
        return Stream.of(
                Arguments.of("1"),
                Arguments.of("10:pjwstk"),
                Arguments.of("pjwstk:10"),
                Arguments.of(""),
                Arguments.of("1"),
                Arguments.of("1:"),
                Arguments.of("i2e"),
                Arguments.of("-10"),
                Arguments.of("0"),
                Arguments.of("6pjwstk")
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTestDecodeStringThrows")
    void testDecodeStringThrows(String encoded) {
        Assertions.assertThrows(DecodingError.class, () -> BString.decode(encoded));
    }

    static Stream<Arguments> sourceTestEncodeString() {
        return Stream.of(
                Arguments.of("6:pjwstk"),
                Arguments.of("7:abcdefg"),
                Arguments.of("1:a"),
                Arguments.of("0:")
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTestEncodeString")
    void testEncodeString(String encoded) {
        final var bencode = new Bencode(encoded);
        Assertions.assertEquals(encoded, bencode.encode());
    }




}