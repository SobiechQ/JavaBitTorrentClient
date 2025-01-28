import com.squareup.okhttp.*;

import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        final var torrent = Torrent.fromFile(new File("src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent")).get();


         var announce = torrent.getAnnounce();

        System.out.println(torrent.getInfoHash());

        var left = "0";

        final var request = new Request.Builder()
                .get()
                .url(HttpUrl.parse(announce)
                        .newBuilder()
                        .addEncodedQueryParameter("info_hash", torrent.getInfoHash())
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
            System.out.println(request);
            Response response = client.newCall(request).execute();
            ResponseBody body = response.body();
            final var read = new BufferedInputStream(body.byteStream()).readAllBytes();
            System.out.println(new String(read));



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

}