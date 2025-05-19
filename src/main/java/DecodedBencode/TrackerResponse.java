package DecodedBencode;

import Bencode.Bencode;
import Bencode.DecodingError;
import org.jooq.lambda.Collectable;
import org.jooq.lambda.Seq;

import java.util.stream.Stream;

public class TrackerResponse extends DecodedBencode {

    public TrackerResponse(Bencode bencode) {
        super(bencode);
    }

    public TrackerResponse(String string) {
        super(string);
    }

    public long getInterval() {
        return this.getBencode()
                .asDictionary("interval")
                .flatMap(Bencode::asInteger)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper Announce"));
    }

    private String getPeersString() {
        return this.getBencode()
                .asDictionary("peers")
                .flatMap(Bencode::asString)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper Announce"));
    }

    public Stream<Peer> getPeers() {
        return Seq.seq(this.getPeersString().chars().boxed())
                .sliding(6)
                .map(Collectable::toUnmodifiableList)
                .map(l -> new Peer(l))
                .stream();
    }




}
