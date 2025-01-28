import Bencode.Bencode;

import java.io.File;
import java.net.URLEncoder;
import java.util.*;
import Bencode.DecodingError;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
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

    public String getAnnounce() {
        return this.getTorrentFile()
                .asDictionary("announce")
                .flatMap(Bencode::asString)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public Long getLength() {
        return this.getTorrentFile()
                .asDictionary("info")
                .flatMap(b->b.asDictionary("length"))
                .flatMap(Bencode::asInteger)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getComment() {
        return this.getTorrentFile()
                .asDictionary("comment")
                .flatMap(Bencode::asString)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getCreatedBy() {
        return this.getTorrentFile()
                .asDictionary("created by")
                .flatMap(Bencode::asString)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public Date getCreationDate() {
        return this.getTorrentFile()
                .asDictionary("creation date")
                .flatMap(Bencode::asInteger)
                .map(Date::new)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getInfoHash() {
        final var charset = Charsets.ISO_8859_1;

        //noinspection UnstableApiUsage
        return this.getTorrentFile()
                .asDictionary("info")
                .map(Bencode::toString)
                .map(s -> Hashing.sha1().hashString(s, charset))
                .map(HashCode::asBytes)
                .map(b -> URLEncoder.encode(new String(b, charset), charset))
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }
}
