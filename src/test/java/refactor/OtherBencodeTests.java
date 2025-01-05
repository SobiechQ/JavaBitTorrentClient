package refactor;

import Bencode.Bencode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

public class OtherBencodeTests {
    @Test
    void testBencode() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var bencode = Bencode.fromFile(new File(location));
        Assertions.assertTrue(bencode.isPresent());


        bencode
                .flatMap(b->b.asDictionary("info"))
                .flatMap(b->b.asDictionary("pieces"))

                .ifPresent(System.out::println);
    }
}
