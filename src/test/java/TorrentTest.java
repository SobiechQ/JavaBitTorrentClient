import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Stream;
import Bencode.DecodingError;

class TorrentTest {

    static Stream<Arguments> sourceTestByteArrayToHex() {
        return Stream.of(
                Arguments.of(new Byte[]{10}, "0a"),
                Arguments.of(new Byte[]{10, 11, 12}, "0a0b0c"),
                Arguments.of(new Byte[]{0}, "00"),
                Arguments.of(new Byte[]{}, ""),
                Arguments.of(new Byte[]{(byte) 232, 118, (byte) 246, 122, 42, (byte) 136, (byte) 134, (byte) 232, (byte) 243, 107, 19, 103, 38, (byte) 195, 15, (byte) 162, (byte) 151, 3, 2, 45}, "e876f67a2a8886e8f36b136726c30fa29703022d"),
                Arguments.of(new Byte[]{10, 11, 12, 13, 14, 15}, "0a0b0c0d0e0f"),
                Arguments.of(new Byte[]{(byte) 232, 118, (byte) 246, 122, 42, (byte) 136}, "e876f67a2a88"),
                Arguments.of(new Byte[]{(byte) 134, (byte) 232, (byte) 243, 107, 19, 103}, "86e8f36b1367"),
                Arguments.of(new Byte[]{38, (byte) 195, 15, (byte) 162}, "26c30fa2"),
                Arguments.of(new Byte[]{(byte) 151, 3, 2, 45}, "9703022d"),
                Arguments.of(new Byte[]{0, (byte) 255, 127, (byte) 128}, "00ff7f80"),
                Arguments.of(new Byte[]{10, (byte) 200, 33, (byte) 129}, "0ac82181"),
                Arguments.of(new Byte[]{(byte) 180, 45, 75, (byte) 220}, "b42d4bdc"),
                Arguments.of(new Byte[]{(byte) 170, (byte) 187, (byte) 204, (byte) 221}, "aabbccdd"),
                Arguments.of(new Byte[]{(byte) 0xaa, 0x55, (byte) 0xff, 0x00}, "aa55ff00"),
                Arguments.of(new Byte[]{1, 35, 69, 103, (byte) 139, (byte) 171}, "012345678bab"),
                Arguments.of(new Byte[]{(byte) 205, (byte) 239, (byte) 254}, "cdeffe"),
                Arguments.of(new Byte[]{0, 0, 0, 1}, "00000001"),
                Arguments.of(new Byte[]{0, 0, (byte) 128, (byte) 255}, "000080ff")
        );
    }

    @ParameterizedTest
    @MethodSource("sourceTestByteArrayToHex")
    void testByteArrayToHex(Byte[] bytes, String hexValue) {
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
                        .allMatch(s -> s.size() == 20);


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

    @Test
    void testGetLength() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var length = torrent.flatMap(Torrent::getLength);

        Assertions.assertTrue(length.isPresent());
        Assertions.assertEquals(661651456, length.get());
    }

    static Stream<Arguments> sourceTestGetHashesString() {
        return Stream.of(
                Arguments.of(0, "45073BD3FB6C7EBF496F86E696A068340714CBCF"),
                Arguments.of(1, "CA8AA052BC999CA7C32B4A7E22E657AE23D66FF9"),
                Arguments.of(2, "33D955927F158E3E187E5183331E86FECA432DA9"),
                Arguments.of(3, "00ED6DE9344F78E267598333076CB6FB8173AE9D"),
                Arguments.of(4, "75E465262315EC3459DECBBC29F280F2B3AF839C"),
                Arguments.of(5, "61026CC992E17EBB3B644733F192392B939C08A2"),
                Arguments.of(6, "90F39728B52D5989CB795D12055C56779A98626F"),
                Arguments.of(7, "2E5F0CAE26252E495587A9A7616ED70349781553"),
                Arguments.of(8, "390BBB0FA5E0CB6DEEF0104C8AAA630AA3D1F96D"),
                Arguments.of(9, "3891FAFAC9A153F9D1935E48ECF67294025DBFA2"),
                Arguments.of(10, "9B10B44B715ED46A76E5FE3F6B9FE4BE4930E05F"),
                Arguments.of(11, "A4C0A93365C8B1C9CDECEA42B592B87CAF7D5560"),
                Arguments.of(12, "9CDBAFFADA32E10F79BCA03BF532080749BED39B"),
                Arguments.of(13, "ABD9164DB72567F3116D39AE75208E8F72F88755"),
                Arguments.of(14, "2C16B79182B43AA06091C7AA1BF3D69B48BEC8AF"),
                Arguments.of(15, "46EDBA3C5E5AFC43A2CB4665387A615105DB7281"),
                Arguments.of(16, "BBB63FAAA5F2C50539F40A4943494BF5FCD527E0"),
                Arguments.of(17, "6C8C4A599B8ECA05A8C50AF231183C10CF7E9EBB"),
                Arguments.of(18, "232B9AF7DDD61B7C65991D91AA665204CC4EBA84"),
                Arguments.of(19, "745FA3F700AB6099AF7C6770866C84DBF9CFC4CC"),
                Arguments.of(20, "AA2F22CDB395C0AFBC87FA623C894F5BFB5FDEA2"),
                Arguments.of(21, "91468AA06C55CADA73124FDD6929365FCF582809"),
                Arguments.of(22, "6A0FDD77E13A28B9041EDEB89A4D5844F5581851")
        );
    }
    @Test
    void testGetComment() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .flatMap(Torrent::getComment)
                .orElseThrow(() -> new DecodingError(""));

        Assertions.assertEquals("\"Debian CD from cdimage.debian.org\"", comment);

    }
    @Test
    void testGetCreatedBy() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .flatMap(Torrent::getCreatedBy)
                .orElseThrow(() -> new DecodingError(""));

        Assertions.assertEquals("mktorrent 1.1", comment);

    }
    @Test
    void testGetCreationDate() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .flatMap(Torrent::getCreationDate)
                .orElseThrow(() -> new DecodingError(""));

        final var calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JANUARY, 16, 8, 50, 48);

        Assertions.assertEquals(calendar.getTime(), comment);

    }

    @ParameterizedTest
    @MethodSource("sourceTestGetHashesString")
    void testGetHashesString(int index, String hash) {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var hashOn = torrent
                .stream()
                .flatMap(Torrent::getHashesString)
                .skip(index)
                .findFirst();

        Assertions.assertEquals(hash.toLowerCase(), hashOn.orElseThrow(()-> new DecodingError("")));
    }

}