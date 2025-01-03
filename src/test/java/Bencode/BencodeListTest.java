package Bencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class BencodeListTest {

    static Stream<Arguments> sourceTestDecodeList() throws DecodingError {
        return Stream.of(
                Arguments.of("l1:ae", List.of(new Bencode("1:a")), ""),
                Arguments.of("le", List.of(), ""),
                Arguments.of("l1:a1:b1:c1:de", List.of(new Bencode("1:a"), new Bencode("1:b"), new Bencode("1:c"), new Bencode("1:d")), ""),
                Arguments.of("l1:a1:b1:c1:de1:a", List.of(new Bencode("1:a"), new Bencode("1:b"), new Bencode("1:c"), new Bencode("1:d")), "1:a"),
                Arguments.of("l1:a1:b1:c1:dee", List.of(new Bencode("1:a"), new Bencode("1:b"), new Bencode("1:c"), new Bencode("1:d")), "e"),
                Arguments.of("li1ei2ei3ee", List.of(new Bencode("i1e"), new Bencode("i2e"), new Bencode("i3e")), ""),
                Arguments.of("li1ei2ei3eee", List.of(new Bencode("i1e"), new Bencode("i2e"), new Bencode("i3e")), "e"),
                Arguments.of("li1ei2ei3eeli1ei2ei3ee", List.of(new Bencode("i1e"), new Bencode("i2e"), new Bencode("i3e")), "li1ei2ei3ee"),
                Arguments.of("li1e1:2i3e1:4ee", List.of(new Bencode("i1e"), new Bencode("1:2"), new Bencode("i3e"), new Bencode("1:4")), "e"),
                Arguments.of("l6:polish8:japanesei-12345e3:cose", List.of(new Bencode("6:polish"), new Bencode("8:japanese"), new Bencode("i-12345e"), new Bencode("3:cos")), ""),
                Arguments.of("ll1:aee", List.of(new Bencode("l1:ae")), ""),
                Arguments.of("ll1:a1:be1:ce", List.of(new Bencode("l1:a1:be"), new Bencode("1:c")), ""),
                Arguments.of("l5:ulicel3:raz3:dwai2ee6:numeryli-1e3:-50i-100eee", List.of(new Bencode("5:ulice"), new Bencode("l3:raz3:dwai2ee"), new Bencode("6:numery"), new Bencode("li-1e3:-50i-100ee")), ""),
                Arguments.of("l5:ulicel3:raz3:dwai2ee6:numeryli-1e3:-50i-100eeee", List.of(new Bencode("5:ulice"), new Bencode("l3:raz3:dwai2ee"), new Bencode("6:numery"), new Bencode("li-1e3:-50i-100ee")), "e"),
                Arguments.of("ld3:key5:valueed3:key5:valueed3:key5:valueee", List.of(new Bencode("d3:key5:valuee"), new Bencode("d3:key5:valuee"), new Bencode("d3:key5:valuee")), ""),
                Arguments.of("ld3:key5:valueed3:key5:valueed3:key5:valueeee", List.of(new Bencode("d3:key5:valuee"), new Bencode("d3:key5:valuee"), new Bencode("d3:key5:valuee")), "e")
        );

    }
    @ParameterizedTest
    @MethodSource("sourceTestDecodeList")
    void testDecodeList(String encoded, List<Bencode> decoded, String remaining) throws DecodingError{
        final var tuple = Bencode.decode(encoded);
        Assertions.assertEquals(remaining, tuple.v2);

        Assertions.assertTrue(tuple.v1.asList().isPresent());

        final var bencode = new Bencode(encoded);

        Assertions.assertTrue(bencode.asList().isPresent());
        Assertions.assertFalse(bencode.asString().isPresent());
        Assertions.assertFalse(bencode.asInteger().isPresent());
        Assertions.assertFalse(bencode.asDictionary().isPresent());
        Assertions.assertEquals(decoded, bencode.asList().get());
    }

    static Stream<Arguments> sourceTestEncodeList() {
        return Stream.of(
                Arguments.of("le"),
                Arguments.of("li0ee"),
                Arguments.of("l1:a2:bb3:ccci0ee"),
                Arguments.of("l1:a2:bb3:ccci-10ei0ei20ee")
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTestEncodeList")
    void testEncodeList(String encoded) {
        final var bencode = new Bencode(encoded);
        Assertions.assertEquals(encoded, bencode.encode());
    }

}