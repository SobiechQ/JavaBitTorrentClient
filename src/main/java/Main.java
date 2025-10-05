import DecodedBencode.Torrent;
import TCP.*;
import Tracker.MultitrackerController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class Main {
    private final static Logger logger = LogManager.getLogger();

    public static void main(String[] args) throws Exception {
        final var torrent = Torrent.fromFile(new File("src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent"));
        final var multi = new MultitrackerController(torrent);
        final var iter = multi.iterator();


        var tracker = iter.next().findFirst().get();

        final var peer = tracker.connect().getPeers()
                .filter(p->p.port()!=1)
                .findFirst()
                .get();



        PeerController peerController = new PeerController(torrent);
        peerController.open(peer);


    }

    public static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

}