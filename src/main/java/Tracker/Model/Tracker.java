package Tracker.Model;

import Model.DecodedBencode.Torrent;
import Tracker.Model.Messages.TrackerRequestProjection;
import Tracker.Model.Messages.TrackerResponse;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

@Data
@ToString(exclude = "torrent")
public abstract class Tracker {
    private final URI uri;
    private final Torrent torrent;
    @Nullable
    private Long lastSeen;
    @Nullable
    private Long interval;

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

    public boolean shouldAnnounce() {
        final var now = System.currentTimeMillis();

        return this.getLastSeen()
                .map(l -> now - l)
                .flatMap(l -> this.getInterval().map(i -> l >= i))
                .orElse(true);
    }

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    private Optional<Long> getLastSeen() {
        return Optional.ofNullable(this.lastSeen);
    }

    private Optional<Long> getInterval() {
        return Optional.ofNullable(this.interval);
    }

    public abstract TrackerResponse announce(TrackerRequestProjection requestProjection) throws IOException;
}
