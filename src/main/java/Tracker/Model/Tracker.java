package Tracker.Model;

import Model.DecodedBencode.Torrent;
import lombok.Data;
import lombok.NonNull;

import java.net.URI;

@Data
public abstract class Tracker {
    private final URI uri;
    private final Torrent torrent;

    public Tracker(@NonNull URI uri, @NonNull Torrent torrent) {
        this.uri = uri;
        this.torrent = torrent;
    }


}
