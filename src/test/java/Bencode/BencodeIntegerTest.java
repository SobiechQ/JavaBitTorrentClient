package Bencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BencodeIntegerTest {

    @Test
    void bencodeIntegerConstructor_whenGivenEncoded_decodesCorrectly() {
        helper_bencodeIntegerConstructor_whenGivenEncoded_decodesCorrectly("i0e", 0, "");
        helper_bencodeIntegerConstructor_whenGivenEncoded_decodesCorrectly("i10ei10e", 10, "i10e");
        helper_bencodeIntegerConstructor_whenGivenEncoded_decodesCorrectly("i123456ee", 123456, "e");
        helper_bencodeIntegerConstructor_whenGivenEncoded_decodesCorrectly("i-10ei", -10, "i");
        helper_bencodeIntegerConstructor_whenGivenEncoded_decodesCorrectly("i-42e4242", -42, "4242");
        helper_bencodeIntegerConstructor_whenGivenEncoded_decodesCorrectly("i-0e", 0, "");
    }

    void helper_bencodeIntegerConstructor_whenGivenEncoded_decodesCorrectly(String encoded, long decoded, String remaining) {
        try {
            final var tuple = Bencode.decodeInteger(encoded);

            Assertions.assertTrue(tuple.isPresent());

            Assertions.assertEquals(decoded, tuple.get().v1);
            Assertions.assertEquals(remaining, tuple.get().v2);

            final var bencode = new Bencode(encoded);

            Assertions.assertTrue(bencode.asInteger().isPresent());
            Assertions.assertEquals(decoded, bencode.asInteger().get());

        } catch (DecodingError e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void bencodeIntegerConstructor_whenGivenFaulty_decodesCorrectly() {
        helper_bencodeIntegerConstructor_whenGivenFaulty_throwsDecodingException("i10");
        helper_bencodeIntegerConstructor_whenGivenFaulty_throwsDecodingException("ie");
        helper_bencodeIntegerConstructor_whenGivenFaulty_throwsDecodingException("iae");
        helper_bencodeIntegerConstructor_whenGivenFaulty_throwsDecodingException("i21.37e");
        helper_bencodeIntegerConstructor_whenGivenFaulty_throwsDecodingException("i21,37e");
    }

    void helper_bencodeIntegerConstructor_whenGivenFaulty_throwsDecodingException(String encoded) {
        Assertions.assertThrows(DecodingError.class, () -> Bencode.decodeInteger(encoded));
    }

}