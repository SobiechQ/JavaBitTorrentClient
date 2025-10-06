package Tracker.Model;

import Model.DecodedBencode.Torrent;
import lombok.NonNull;

import java.net.URI;

public class HttpTracker extends Tracker {

    public HttpTracker(@NonNull URI uri, @NonNull Torrent torrent) {
        super(uri, torrent);
    }

//    @Override
//    public TrackerResponse connect() throws IOException {
//        final var request = new Request.Builder()
//                .get()
//                .url(HttpUrl.parse(this.getUri().toString())
//                        .newBuilder()
//                        .addEncodedQueryParameter("info_hash", this.getTorrent().getInfoHashUrl())
//                        .addQueryParameter("peer_id", "00112233445566778899")
//                        .addQueryParameter("port", "6881")
//                        .addQueryParameter("uploaded", "0")
//                        .addQueryParameter("downloaded", "0")
//                        .addQueryParameter("left", "0")
//                        .addQueryParameter("compact", "1")
//                        .build())
//                .build();
//
//        final var response = new OkHttpClient().
//                newCall(request)
//                .execute();
//
//        try(final var body = response.body()){
//            return new TrackerResponse(new Bencode(body.byteStream().readAllBytes()));
//        }
//    }
}
