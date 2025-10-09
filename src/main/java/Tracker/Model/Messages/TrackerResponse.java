package Tracker.Model.Messages;

import Model.Bencode.Bencode;
import Model.Bencode.DecodingError;
import Model.DecodedBencode.DecodedBencode;
import Peer.Model.Peer;
import Tracker.Model.Tracker;
import lombok.Getter;
import org.jooq.lambda.Collectable;
import org.jooq.lambda.Seq;

import java.util.stream.Stream;

@Getter
public class TrackerResponse extends DecodedBencode {

    private final Tracker respondTo;

    public TrackerResponse(Tracker tracker, Bencode bencode) {
        super(bencode);
        this.respondTo = tracker;
    }

    public TrackerResponse(Tracker tracker, String string) {
        super(string);
        this.respondTo = tracker;
    }

    public long getInterval() {
        return this.getBencode()
                .asDictionary("interval")
                .flatMap(Bencode::asInteger)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper Announce"));
    }

    public Stream<Peer> getPeers() {
        return Seq.seq(this.getPeersString().chars().boxed())
                .sliding(6)
                .map(Collectable::toUnmodifiableList)
                .map(Peer::new)
                .stream();
    }

    private String getPeersString() {
        return this.getBencode()
                .asDictionary("peers")
                .flatMap(Bencode::asString)
                .orElseThrow(()-> new DecodingError("Provided bencode is not proper Announce"));
    }

    @Override
    public String toString() {
        return "TrackerResponse{" +
               "respondTo=" + respondTo +
               "} " + super.toString();
    }
}
