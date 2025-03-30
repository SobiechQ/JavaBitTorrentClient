package DecodedBencode;

import Bencode.Bencode;
import Bencode.DecodingError;
import org.jooq.lambda.Collectable;
import org.jooq.lambda.Seq;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.stream.Stream;

public class Announce extends DecodedBencode {

    public Announce(Bencode bencode) {
        super(bencode);
    }

    public Announce(String string) {
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
                .map(Announce::getPeerByBytes)
                .stream();
    }

    private static Peer getPeerByBytes(List<Integer> src) {
        final var address = new byte[4];
        for (int i = 0; i < 4; i++)
            address[i] = (byte) src.get(i).intValue();

        try {
            return new Peer(InetAddress.getByAddress(address), src.get(4) * 256 + src.get(5));
        } catch (UnknownHostException e) {
            throw new DecodingError(e);
        }
    }


}
