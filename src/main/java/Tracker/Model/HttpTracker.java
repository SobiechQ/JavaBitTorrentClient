package Tracker.Model;

import Model.Bencode.Bencode;
import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Messages.TrackerResponse;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import lombok.NonNull;

import java.io.IOException;
import java.net.URI;

public class HttpTracker extends Tracker {

    public HttpTracker(@NonNull URI uri, @NonNull Torrent torrent) {
        super(uri, torrent);
    }

    @Override
    public TrackerResponse announce(TrackerRequestProjection requestProjection) throws IOException {
        final var request = new Request.Builder()
                .get()
                .url(HttpUrl.get(requestProjection.url())
                        .newBuilder()
                        .addEncodedQueryParameter("info_hash", requestProjection.infoHashUrl())
                        .addQueryParameter("peer_id", "00112233445566778899")
                        .addQueryParameter("port", "6881")
                        .addQueryParameter("uploaded", String.valueOf(requestProjection.uploaded()))
                        .addQueryParameter("downloaded", String.valueOf(requestProjection.downloaded()))
                        .addQueryParameter("left", String.valueOf(requestProjection.left()))
                        .addQueryParameter("compact", "1")
                        .build())
                .build();

        final var response = new OkHttpClient().
                newCall(request)
                .execute();

        try(final var body = response.body()){
            return new TrackerResponse(this, new Bencode(body.byteStream().readAllBytes()));
        }
    }
}
