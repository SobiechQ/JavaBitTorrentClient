import Bencode.Bencode;

import java.io.File;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import Bencode.DecodingError;
import lombok.Getter;
import okhttp3.HttpUrl;
import okhttp3.Request;

@Getter
public class Torrent { //todo abstractify? wszystkie klasy bedace reprezentacją jakiegoś bencoda

    private final Bencode torrentFile;

    public Torrent(Bencode bencode) {
        this.torrentFile = bencode;
    }

    public static Optional<Torrent> fromFile(File file) {
        return Bencode.fromFile(file)
                .map(Torrent::new);
    }

    public Stream<byte[]> getHashesBytes() {
        return this.torrentFile.asDictionary("info")
                .flatMap(b -> b.asDictionary("pieces"))
                .flatMap(Bencode::asString)
                .map(String::getBytes)
                .stream()
                .peek(s -> System.out.println(Arrays.toString(s)))
                .flatMap(s -> IntStream.iterate(0, i -> i + 20)
                        .limit(1 + (s.length / 20))
                        .mapToObj(i -> Arrays.copyOfRange(s, i, i + 20))
                );
    }

    public Stream<String> getHashesString() {
        return this.getHashesBytes()
                .map(Torrent::byteArrayToHex);
    }

    public static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x", b >= 0 ? b : 256 + b));
        return sb.toString();
    }
    public Optional<String> getAnnounce() {
        return this.getTorrentFile()
                .asDictionary("announce")
                .flatMap(b->b.asString());
    }
    public void announce() {
//        this.getAnnounce().map(host -> {
//
//        })
//




    }


}
