package Model.DecodedBencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;

class TorrentTest {

    @Test
    void testGetAnnounce() throws IOException {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var announce = torrent.getAnnounce();

        Assertions.assertEquals(announce, URI.create("http://bttracker.debian.org:6969/announce"));
    }

    @Test
    void testGetLength() throws IOException {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var length = torrent.getLength();

        Assertions.assertEquals(661651456, length);
    }

    @Test
    void testGetPieceLength() throws IOException {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var length = torrent.getPieceLength();

        Assertions.assertEquals(262144, length);
    }

    @Test
    void testGetLengthCalculated() throws IOException {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var pieceLength = torrent.getPieceLength();
        final var pieceCount = torrent
                .getPieceHash()
                .count();
        final var totalLength = torrent.getLength();

        Assertions.assertEquals(totalLength, pieceLength * pieceCount);

    }

    @Test
    void testGetComment() throws IOException {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .getComment();

        Assertions.assertEquals("\"Debian CD from cdimage.debian.org\"", comment);

    }

    @Test
    void testGetCreatedBy() throws IOException {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .getCreatedBy();

        Assertions.assertEquals("mktorrent 1.1", comment);

    }

    @Test
    void testGetCreationDate() throws IOException {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var comment = torrent
                .getCreationDate();

        final var calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JANUARY, 16, 8, 50, 48);

        Assertions.assertEquals(calendar.getTime(), comment);

    }

    @Test
    void testGetInfoHashUrl() throws IOException {
        final var location = "src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent";
        final var torrent = Torrent.fromFile(new File(location));

        final var infoHash = torrent
                .getInfoHashUrl();

        Assertions.assertEquals(infoHash, "Z-n%A2Ko%9B%BA%3D%E0%1B%D2%84h%E8%60%BD%DAZ%ED");
    }


}