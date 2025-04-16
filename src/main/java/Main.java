import Bencode.Bencode;
import DecodedBencode.Announce;
import DecodedBencode.Torrent;
import TCP.Handshake;
import TCP.MessageType;
import TCP.PeerMessage;
import com.squareup.okhttp.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class Main {
    private final static Logger logger = LogManager.getLogger();
    public static void main(String[] args) {
        final var torrent = Torrent.fromFile(new File("src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent")).get();

        var announce = torrent.getAnnounce();


        var left = "0";

        final var request = new Request.Builder()
                .get()
                .url(HttpUrl.parse(announce)
                        .newBuilder()
                        .addEncodedQueryParameter("info_hash", torrent.getInfoHashUrl())
                        .addQueryParameter("peer_id", "00112233445566778899")
                        .addQueryParameter("port", "6881")
                        .addQueryParameter("uploaded", "0")
                        .addQueryParameter("downloaded", "0")
                        .addQueryParameter("left", String.valueOf(left))
                        .addQueryParameter("compact", "1")
                        .build())
                .build();
        try {
            OkHttpClient client = new OkHttpClient();
//            System.out.println(request);
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            final var read = new BufferedInputStream(body.byteStream()).readAllBytes();

            final var an = new Announce(new Bencode(read));
            final var p = an.getPeers().findFirst().get();

//            an.getPeers().forEach(System.out::println);
            logger.info("Connecting... {}:{}", p.address(), p.port());
            try (final var socket = new Socket(p.address(), p.port())) {
                logger.info("Socket connection opened to {}", socket.getInetAddress());
                final var hand = new Handshake(torrent.getInfoHash(), "00112233445566778899".getBytes());

                hand.get(socket);


                final var in = socket.getInputStream();
                var msg = PeerMessage.get(in);

//                System.out.println(msg);

                final var resp = new PeerMessage(MessageType.INTERESTED);
                resp.send(socket.getOutputStream());

                msg = PeerMessage.get(in);
//                System.out.println(msg);

                final var peerRequest = new PeerMessage(MessageType.REQUEST, 0, 0, (int) Math.pow(2, 14));
                peerRequest.send(socket.getOutputStream());


                for (int i = 0; i < 150; i++) {
                    msg = PeerMessage.get(in);

                }





//                try (BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream())) {
//                    byte[] resp =  bufferedInputStream.readAllBytes();
//                    System.out.println(new String(resp));
//                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static String encodeInfoHash(List<Byte> infoHashBytes) {
        StringBuilder encoded = new StringBuilder();
        for (byte b : infoHashBytes) {
            encoded.append('%').append(String.format("%02X", b & 0xFF));
        }
        return encoded.toString();
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