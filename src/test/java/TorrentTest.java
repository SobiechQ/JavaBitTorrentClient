import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.stream.Stream;

class TorrentTest {

    @Test
    void testGetHashes() {


    }

    static Stream<Arguments> sourceTestByteArrayToHex() {
        return Stream.of(
                Arguments.of(new byte[]{10}, "0a"),
                Arguments.of(new byte[]{10, 11, 12}, "0a0b0c"),
                Arguments.of(new byte[]{0}, "00"),
                Arguments.of(new byte[]{}, ""),
                Arguments.of(new byte[]{(byte) 232, 118, (byte) 246, 122, 42, (byte) 136, (byte) 134, (byte) 232, (byte) 243, 107, 19, 103, 38, (byte) 195, 15, (byte) 162, (byte) 151, 3, 2, 45}, "e876f67a2a8886e8f36b136726c30fa29703022d"),
                Arguments.of(new byte[]{10, 11, 12, 13, 14, 15}, "0a0b0c0d0e0f"),
                Arguments.of(new byte[]{(byte) 232, 118, (byte) 246, 122, 42, (byte) 136}, "e876f67a2a88"),
                Arguments.of(new byte[]{(byte) 134, (byte) 232, (byte) 243, 107, 19, 103}, "86e8f36b1367"),
                Arguments.of(new byte[]{38, (byte) 195, 15, (byte) 162}, "26c30fa2"),
                Arguments.of(new byte[]{(byte) 151, 3, 2, 45}, "9703022d"),
                Arguments.of(new byte[]{0, (byte) 255, 127, (byte) 128}, "00ff7f80"),
                Arguments.of(new byte[]{10, (byte) 200, 33, (byte) 129}, "0ac82181"),
                Arguments.of(new byte[]{(byte) 180, 45, 75, (byte) 220}, "b42d4bdc"),
                Arguments.of(new byte[]{(byte) 170, (byte) 187, (byte) 204, (byte) 221}, "aabbccdd"),
                Arguments.of(new byte[]{(byte) 0xaa, 0x55, (byte) 0xff, 0x00}, "aa55ff00"),
                Arguments.of(new byte[]{1, 35, 69, 103, (byte) 139, (byte) 171}, "012345678bab"),
                Arguments.of(new byte[]{(byte) 205, (byte) 239, (byte) 254}, "cdeffe"),
                Arguments.of(new byte[]{0, 0, 0, 1}, "00000001"),
                Arguments.of(new byte[]{0, 0, (byte) 128, (byte) 255}, "000080ff")
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTestByteArrayToHex")
    void testByteArrayToHex(byte[] bytes, String hexValue) {
        Assertions.assertEquals(hexValue, Torrent.byteArrayToHex(bytes));
    }

    @Test
    void testGetHashesAll40Characters() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var count = torrent
                .stream()
                .flatMap(Torrent::getHashesString)
                .count();

        Assertions.assertTrue(count > 1);

        final var allString40Length = torrent
                .map(Torrent::getHashesString)
                .stream()
                .flatMap(s -> s)
                .allMatch(s -> s.length() == 40);

        final var allByteArrays20Length =
                torrent
                        .map(Torrent::getHashesBytes)
                        .stream()
                        .flatMap(s -> s)
                        .allMatch(s -> s.length == 20);


        Assertions.assertTrue(allString40Length);
        Assertions.assertTrue(allByteArrays20Length);
    }
    @Test
    void testGetAnnounce() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var announce = torrent.flatMap(Torrent::getAnnounce);

        Assertions.assertTrue(announce.isPresent());
        Assertions.assertEquals(announce.get(), "http://bttracker.debian.org:6969/announce");
    }
}