package Bencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class BencodeListTest {
    //todo list of dictionary
    //todo unsecessfull lists
    @Test
    void bencodeDecodeList_whenGivenEncoded_decodesCorrectly() throws DecodingError {
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("l1:ae", List.of(new Bencode("1:a")), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("le", List.of(), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("l1:a1:b1:c1:de", List.of(new Bencode("1:a"), new Bencode("1:b"), new Bencode("1:c"), new Bencode("1:d")), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("l1:a1:b1:c1:de1:a", List.of(new Bencode("1:a"), new Bencode("1:b"), new Bencode("1:c"), new Bencode("1:d")), "1:a");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("l1:a1:b1:c1:dee", List.of(new Bencode("1:a"), new Bencode("1:b"), new Bencode("1:c"), new Bencode("1:d")), "e");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("li1ei2ei3ee", List.of(new Bencode("i1e"), new Bencode("i2e"), new Bencode("i3e")), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("li1ei2ei3eee", List.of(new Bencode("i1e"), new Bencode("i2e"), new Bencode("i3e")), "e");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("li1ei2ei3eeli1ei2ei3ee", List.of(new Bencode("i1e"), new Bencode("i2e"), new Bencode("i3e")), "li1ei2ei3ee");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("li1e1:2i3e1:4ee", List.of(new Bencode("i1e"), new Bencode("1:2"), new Bencode("i3e"), new Bencode("1:4")), "e");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("l6:polish8:japanesei-12345e3:cose", List.of(new Bencode("6:polish"),new Bencode("8:japanese"), new Bencode("i-12345e"), new Bencode("3:cos")), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("ll1:aee", List.of(new Bencode("l1:ae")), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("ll1:a1:be1:ce", List.of(new Bencode("l1:a1:be"), new Bencode("1:c")), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("l5:ulicel3:raz3:dwai2ee6:numeryli-1e3:-50i-100eee", List.of(new Bencode("5:ulice"), new Bencode("l3:raz3:dwai2ee"), new Bencode("6:numery"), new Bencode("li-1e3:-50i-100ee")), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("l5:ulicel3:raz3:dwai2ee6:numeryli-1e3:-50i-100eeee", List.of(new Bencode("5:ulice"), new Bencode("l3:raz3:dwai2ee"), new Bencode("6:numery"), new Bencode("li-1e3:-50i-100ee")), "e");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("ld3:key5:valueed3:key5:valueed3:key5:valueee", List.of(new Bencode("d3:key5:valuee"), new Bencode("d3:key5:valuee"), new Bencode("d3:key5:valuee")), "");
        helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly("ld3:key5:valueed3:key5:valueed3:key5:valueeee", List.of(new Bencode("d3:key5:valuee"), new Bencode("d3:key5:valuee"), new Bencode("d3:key5:valuee")), "e");

    }

    void helper_bencodeDecodeList_whenGivenEncoded_decodesCorrectly(String encoded, List<Bencode> decoded, String remaining) {
        try {
            final var tuple = Bencode.decodeList(encoded);

            Assertions.assertTrue(tuple.isPresent());


            Assertions.assertEquals(decoded, tuple.get().v1);
            Assertions.assertEquals(remaining, tuple.get().v2);

            final var bencode = new Bencode(encoded);

            Assertions.assertTrue(bencode.asList().isPresent());
            Assertions.assertEquals(decoded, bencode.asList().get());


        } catch (DecodingError e) {
            throw new RuntimeException(e);
        }
    }

}