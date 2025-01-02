package Bencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class BencodeStringTest {

    @Test
    void bencodeStringConstructor_whenGivenEncoded_decodesCorrectly() {
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("6:pjwstk", "pjwstk", "");
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("7:abcdefg", "abcdefg", "");
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("1:a", "a", "");
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("2:abcd", "ab", "cd");
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("3:ab:cd", "ab:", "cd");
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("4::::::::::", "::::", ":::::");
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("0:", "", "");
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("0:::", "", "::");
        helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly("0:abcd", "", "abcd");
    }

    void helper_bencodeStringConstructor_whenGivenEncoded_decodesCorrectly(String encoded, String decoded, String remaining) {
        try {
            final var tuple = Bencode.decodeString(encoded);

            Assertions.assertTrue(tuple.isPresent());

            Assertions.assertEquals(decoded, tuple.get().v1);
            Assertions.assertEquals(remaining, tuple.get().v2);

            final var bencode = new Bencode(encoded);

            Assertions.assertTrue(bencode.asString().isPresent());
            Assertions.assertEquals(decoded, bencode.asString().get());

        } catch (DecodingError e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void bencodeStringConstructor_whenGivenFaulty_throwsDecodingException(){
        helper_bencodeStringConstructor_whenGivenFaulty_throwsDecodingException("1");
        helper_bencodeStringConstructor_whenGivenFaulty_throwsDecodingException("10:pjwstk");
        helper_bencodeStringConstructor_whenGivenFaulty_throwsDecodingException("6pjwstk");

    }

    void helper_bencodeStringConstructor_whenGivenFaulty_throwsDecodingException(String encoded) {
        Assertions.assertThrows(DecodingError.class, () -> Bencode.decodeString(encoded));
    }




}