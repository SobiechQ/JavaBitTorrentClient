package DecodedBencode;

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
public class Torrent extends DecodedBencode {

    public Torrent(Bencode bencode) {
        super(bencode);
    }

    public static Optional<Torrent> fromFile(File file) {
        return Bencode.fromFile(file)
                .map(Torrent::new);
    }

    public String getAnnounce() {
        return this.getBencode()
                .asDictionary("announce")
                .flatMap(Bencode::asString)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public long getLength() {
        return this.getBencode()
                .asDictionary("info")
                .flatMap(b->b.asDictionary("length"))
                .flatMap(Bencode::asInteger)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public int getPieceLength() {
        return this.getBencode()
                .asDictionary("info")
                .flatMap( b->b.asDictionary("piece length"))
                .flatMap(Bencode::asInteger)
                .map(Math::toIntExact)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getComment() {
        return this.getBencode()
                .asDictionary("comment")
                .flatMap(Bencode::asString)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getCreatedBy() {
        return this.getBencode()
                .asDictionary("created by")
                .flatMap(Bencode::asString)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public Date getCreationDate() {
        return this.getBencode()
                .asDictionary("creation date")
                .flatMap(Bencode::asInteger)
                .map(Date::new)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

    public String getInfoHashUrl() {
        final var infoHash = this.getInfoHash();

        return URLEncoder.encode(new String(infoHash, Bencode.CHARSET), Bencode.CHARSET);
    }
    public byte[] getInfoHash() {

        //noinspection UnstableApiUsage
        return this.getBencode()
                .asDictionary("info")
                .map(Bencode::toString)
                .map(s -> Hashing.sha1().hashString(s, Bencode.CHARSET))
                .map(HashCode::asBytes)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper torrent file"));
    }

}
