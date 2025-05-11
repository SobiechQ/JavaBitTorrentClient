package DecodedBencode;

import Bencode.Bencode;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Stream;

import Bencode.DecodingError;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import lombok.Getter;

@Getter
public class Torrent extends DecodedBencode {

    public Torrent(Bencode bencode) {
        super(bencode);
    }

    public static Torrent fromFile(File file) throws IOException, DecodingError {
        return new Torrent(Bencode.fromFile(file));
    }

    public URI getAnnounce() {
        return this.getBencode()
                .asDictionary("announce")
                .flatMap(Bencode::asString)
                .map(URI::create)
                .orElseThrow(() -> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public Stream<List<URI>> getAnnounceList() {
        return this.getBencode()
                .asDictionary("announce-list")
                .flatMap(Bencode::asList)
                .stream()
                .flatMap(Collection::stream)
                .flatMap(b -> b.asList().stream())
                .flatMap(b -> b.stream().map(c -> c.asString().stream().toList()))
                .map(l -> l.stream().map(URI::create).toList());

    }

    public long getLength() {
        return this.getBencode()
                .asDictionary("info")
                .flatMap(b -> b.asDictionary("length"))
                .flatMap(Bencode::asInteger)
                .orElseThrow(() -> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public int getPieceLength() {
        return this.getBencode()
                .asDictionary("info")
                .flatMap(b -> b.asDictionary("piece length"))
                .flatMap(Bencode::asInteger)
                .map(Math::toIntExact)
                .orElseThrow(() -> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getComment() {
        return this.getBencode()
                .asDictionary("comment")
                .flatMap(Bencode::asString)
                .orElseThrow(() -> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getCreatedBy() {
        return this.getBencode()
                .asDictionary("created by")
                .flatMap(Bencode::asString)
                .orElseThrow(() -> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public Date getCreationDate() {
        return this.getBencode()
                .asDictionary("creation date")
                .flatMap(Bencode::asInteger)
                .map(Date::new)
                .orElseThrow(() -> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getInfoHashUrl() {
        final var infoHash = this.getInfoHash();

        return URLEncoder.encode(new String(infoHash, Bencode.CHARSET), Bencode.CHARSET);
    }

    public Stream<List<Byte>> getPieceHash() {
        return this.getBencode().asDictionary("info")
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

    public int getPieceCount() {
        return (int) this.getPieceHash().count();
    }

    public Stream<String> getPieceHashString() {
        return this.getPieceHash()
                .map(l -> l.toArray(new Byte[20]))
                .map(Torrent::byteArrayToHex);
    }

    public Optional<List<Byte>> getPieceHash(int index) {
        return this.getPieceHash()
                .skip(index)
                .findFirst();
    }

    public byte[] getInfoHash() {

        //noinspection UnstableApiUsage
        return this.getBencode()
                .asDictionary("info")
                .map(Bencode::toString)
                .map(s -> Hashing.sha1().hashString(s, Bencode.CHARSET))
                .map(HashCode::asBytes)
                .orElseThrow(() -> new DecodingError("Provided bencode is not proper torrent file"));
    }

    private static String byteArrayToHex(Byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes)
            sb.append(String.format("%02x", b >= 0 ? b : 256 + b));
        return sb.toString();
    }

}
