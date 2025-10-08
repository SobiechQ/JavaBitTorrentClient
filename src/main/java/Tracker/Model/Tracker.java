package Tracker.Model;

import Model.DecodedBencode.Torrent;
import lombok.Data;
import lombok.NonNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

@Data
public abstract class Tracker {
    private final URI uri;
    private final Torrent torrent;

    public Tracker(@NonNull URI uri, @NonNull Torrent torrent) {
        this.uri = uri;
        this.torrent = torrent;
    }

    public URL getUrl() {
        try {
            return this.getUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


}
