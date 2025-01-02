package Bencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class BencodeDictionaryTest {
    @Test
    void bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly() throws DecodingError {
        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d4:key16:value1e",
                Map.of("key1", new Bencode("6:value1")), "");
        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("de",
                Map.of(), "");
        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d4:key16:value14:key2i123e4:key3i-55ee",
                Map.of("key1", new Bencode("6:value1"), "key2", new Bencode("i123e"), "key3", new Bencode("i-55e")), "");
        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eee",
                Map.of("names", new Bencode("l1::3:abc3:defi-30ee"), "somethingElse", new Bencode("li-1ei-2ei-3ei4ei5e1:ee"), "key3", new Bencode("le")), "");
        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eeed4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eee",
                Map.of("names", new Bencode("l1::3:abc3:defi-30ee"), "somethingElse", new Bencode("li-1ei-2ei-3ei4ei5e1:ee"), "key3", new Bencode("le")), "d4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eee");

        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d4:key16:value1ee",
                Map.of("key1", new Bencode("6:value1")), "e");
        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("dei-20e",
                Map.of(), "i-20e");
        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d4:key16:value14:key2i123e4:key3i-55ee3::::",
                Map.of("key1", new Bencode("6:value1"), "key2", new Bencode("i123e"), "key3", new Bencode("i-55e")), "3::::");
        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d4:key3le5:namesl1::3:abc3:defi-30ee13:somethingElseli-1ei-2ei-3ei4ei5e1:eeeli1ei2ei3ee",
                Map.of("names", new Bencode("l1::3:abc3:defi-30ee"), "somethingElse", new Bencode("li-1ei-2ei-3ei4ei5e1:ee"), "key3", new Bencode("le")), "li1ei2ei3ee");

        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d10:dictionaryd4:key16:value14:key2i-20ee7:integeri-1234e4:listl3:abci-10ee6:string6:stringe",
                Map.of("string", new Bencode("6:string"), "integer", new Bencode("i-1234e"), "list", new Bencode("l3:abci-10ee"), "dictionary", new Bencode("d4:key16:value14:key2i-20ee")), "");

        helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly("d4:dictd4:dictd4:dictdeeee",
                Map.of("dict", new Bencode("d4:dictd4:dictdeee")),  "");

    }

    void helper_bencodeDecodeDictionary_whenGivenEncoded_decodesCorrectly(String encoded, Map<String, Bencode> decoded, String remaining) {
        try {
            final var tuple = Bencode.decodeDictionary(encoded);

            Assertions.assertTrue(tuple.isPresent());

            Assertions.assertEquals(decoded, tuple.get().v1);
            Assertions.assertEquals(remaining, tuple.get().v2);

            final var bencode = new Bencode(encoded);

            Assertions.assertTrue(bencode.asDictionary().isPresent());
            Assertions.assertEquals(decoded, bencode.asDictionary().get());


        } catch (DecodingError e) {
            throw new RuntimeException(e);
        }
    }

}