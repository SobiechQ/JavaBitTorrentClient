import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        final var torrent = Torrent.fromFile(new File("src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent"));
        String announce = torrent.flatMap(Torrent::getAnnounce).orElseThrow();
        torrent
                .map(t -> t.getTorrentFile())
                .ifPresent(System.out::println);

        torrent.flatMap(t->t.getComment()).ifPresent(System.out::println);

        Byte[] infoHash = torrent.flatMap(t -> t.getHashesBytes().findFirst())
                .orElseThrow().toArray(new Byte[20]);

        byte[] arr = new byte[20];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = infoHash[i];
        }


        final var str = new String(arr, StandardCharsets.ISO_8859_1);
        //E%07%3B%D3%FBl%7E%BFIo%86%E6%96%A0h4%07%14%CB%CF
        

        System.out.println(URLEncoder.encode(str, StandardCharsets.ISO_8859_1));
        System.out.println(infoHash);

    }
    private static String encodeInfoHash(List<Byte> infoHashBytes) {
        StringBuilder encoded = new StringBuilder();
        for (byte b : infoHashBytes) {
            encoded.append('%').append(String.format("%02X", b & 0xFF));
        }
        return encoded.toString();
    }

}