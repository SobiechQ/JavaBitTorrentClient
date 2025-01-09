import Bencode.Bencode;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.Getter;

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

    public Stream<List<Byte>> getHashesBytes() {
        return this.torrentFile.asDictionary("info")
                .flatMap(b -> b.asDictionary("pieces"))
                .flatMap(Bencode::asString)
                .stream()
                .flatMapToInt(String::codePoints)
                .collect(() -> new ArrayList<List<Byte>>(), (buffer, value) -> {
                    if (buffer.isEmpty() || buffer.getLast().size() >= 20)
                        buffer.add(new LinkedList<>());
                    buffer.getLast().add((byte) value);
                }, ArrayList::addAll)
                .stream();
    }

    public Stream<String> getHashesString() {
        return this.getHashesBytes()
                .map(l->l.toArray(new Byte[20]))
                .map(Torrent::byteArrayToHex);
    }

    public Optional<String> getAnnounce() {
        return this.getTorrentFile()
                .asDictionary("announce")
                .flatMap(b->b.asString());
    }

    public Optional<Long> getLength() {
        return this.getTorrentFile()
                .asDictionary("info")
                .flatMap(b->b.asDictionary("length"))
                .flatMap(b->b.asInteger());
    }

    public static String byteArrayToHex(Byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            sb.append(String.format("%02x", b >= 0 ? b : 256 + b));
        return sb.toString();
    }

}
