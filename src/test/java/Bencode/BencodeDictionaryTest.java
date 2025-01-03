package Bencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

class BencodeDictionaryTest {

    static Stream<Arguments> sourceTestDecodeDictionary() throws DecodingError {
        return Stream.of(
                Arguments.of("d4:key16:value1e",
                        Map.of("key1", new Bencode("6:value1")), ""),
                Arguments.of("de",
                        Map.of(), ""),
                Arguments.of("d4:key16:value14:key2i123e4:key3i-55ee",
                        Map.of("key1", new Bencode("6:value1"), "key2", new Bencode("i123e"), "key3", new Bencode("i-55e")), ""),
                Arguments.of("d4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eee",
                        Map.of("names", new Bencode("l1::3:abc3:defi-30ee"), "somethingElse", new Bencode("li-1ei-2ei-3ei4ei5e1:ee"), "key3", new Bencode("le")), ""),
                Arguments.of("d4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eeed4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eee",
                        Map.of("names", new Bencode("l1::3:abc3:defi-30ee"), "somethingElse", new Bencode("li-1ei-2ei-3ei4ei5e1:ee"), "key3", new Bencode("le")), "d4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eee"),
                Arguments.of("d4:key16:value1ee",
                        Map.of("key1", new Bencode("6:value1")), "e"),
                Arguments.of("dei-20e",
                        Map.of(), "i-20e"),
                Arguments.of("d4:key16:value14:key2i123e4:key3i-55ee3::::",
                        Map.of("key1", new Bencode("6:value1"), "key2", new Bencode("i123e"), "key3", new Bencode("i-55e")), "3::::"),
                Arguments.of("d4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eeeli1ei2ei3ee",
                        Map.of("names", new Bencode("l1::3:abc3:defi-30ee"), "somethingElse", new Bencode("li-1ei-2ei-3ei4ei5e1:ee"), "key3", new Bencode("le")), "li1ei2ei3ee"),
                Arguments.of("d10:dictionaryd4:key16:value14:key2i-20ee7:integeri-1234e4:listl3:abci-10ee6:string6:stringe",
                        Map.of("string", new Bencode("6:string"), "integer", new Bencode("i-1234e"), "list", new Bencode("l3:abci-10ee"), "dictionary", new Bencode("d4:key16:value14:key2i-20ee")), ""),
                Arguments.of("d4:dictd4:dictd4:dictdeeee",
                        Map.of("dict", new Bencode("d4:dictd4:dictdeee")), "")
        );
    }
    @ParameterizedTest
    @MethodSource("sourceTestDecodeDictionary")
    void testDecodeDictionary(String encoded, Map<String, Bencode> decoded, String remaining) throws DecodingError {
        final var tuple = Bencode.decode(encoded);

        Assertions.assertEquals(remaining, tuple.v2);
        Assertions.assertTrue(tuple.v1.asDictionary().isPresent());

        final var bencode = new Bencode(encoded);

        Assertions.assertTrue(bencode.asDictionary().isPresent());
        Assertions.assertFalse(bencode.asList().isPresent());
        Assertions.assertFalse(bencode.asInteger().isPresent());
        Assertions.assertFalse(bencode.asString().isPresent());
        Assertions.assertEquals(decoded, bencode.asDictionary().get());
    }

}