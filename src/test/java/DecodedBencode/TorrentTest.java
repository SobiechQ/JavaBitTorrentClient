package DecodedBencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Calendar;

import Bencode.DecodingError;

class TorrentTest {

    @Test
    void testGetAnnounce() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var announce = torrent.map(Torrent::getAnnounce);

        Assertions.assertTrue(announce.isPresent());
        Assertions.assertEquals(announce.get(), "http://bttracker.debian.org:6969/announce");
    }

    @Test
    void testGetLength() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var length = torrent.map(Torrent::getLength);

        Assertions.assertTrue(length.isPresent());
        Assertions.assertEquals(661651456, length.get());
    }

    @Test
    void testGetPieceLength() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var length = torrent.map(Torrent::getPieceLength);

        Assertions.assertTrue(length.isPresent());
        Assertions.assertEquals(262144, length.get());
    }

    @Test
    void testGetLengthCalculated() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var pieceLength = torrent.map(Torrent::getPieceLength);
        final var pieceCount = torrent.stream()
                .flatMap(Torrent::getPieceHash)
                .count();
        final var totalLength = torrent.map(Torrent::getLength);


        Assertions.assertTrue(pieceLength.isPresent());
        Assertions.assertTrue(totalLength.isPresent());

        Assertions.assertEquals(totalLength.get(), pieceLength.get() * pieceCount);

    }

    @Test
    void testGetComment() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .map(Torrent::getComment)
                .orElseThrow(() -> new DecodingError(""));

        Assertions.assertEquals("\"Debian CD from cdimage.debian.org\"", comment);

    }

    @Test
    void testGetCreatedBy() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .map(Torrent::getCreatedBy)
                .orElseThrow(() -> new DecodingError(""));

        Assertions.assertEquals("mktorrent 1.1", comment);

    }

    @Test
    void testGetCreationDate() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .map(Torrent::getCreationDate)
                .orElseThrow(() -> new DecodingError(""));

        final var calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JANUARY, 16, 8, 50, 48);

        Assertions.assertEquals(calendar.getTime(), comment);

    }

    @Test
    void testGetInfoHashUrl() {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var infoHash = torrent
                .map(Torrent::getInfoHashUrl)
                .orElseThrow(() -> new DecodingError(""));

        Assertions.assertEquals(infoHash, "Z-n%A2Ko%9B%BA%3D%E0%1B%D2%84h%E8%60%BD%DAZ%ED");
    }


}