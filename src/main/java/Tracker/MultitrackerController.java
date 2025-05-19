package Tracker;

import DecodedBencode.Torrent;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * https://www.bittorrent.org/beps/bep_0012.html
 */
public class MultitrackerController implements Iterable<Stream<Tracker>> {
    private final Torrent torrent;

    public MultitrackerController(@NonNull Torrent torrent) {
        this.torrent = torrent;
    }

    public Stream<Stream<Tracker>> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator(), Spliterator.ORDERED), false);
    }

    @NotNull
    @Override
    public Iterator<Stream<Tracker>> iterator() {
        return new Iterator<>() {
            private int tier = 0;

            @Override
            public boolean hasNext() {
                return torrent.getAnnounceList()
                               .count() >= tier;
            }

            @Override
            public Stream<Tracker> next() {
                this.tier++;

                if (this.tier - 1 == 0)
                    return Stream.of(torrent.getAnnounce())
                            .map(MultitrackerController.this::toTracker);

                return torrent.getAnnounceList()
                        .skip(tier - 2)
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new)
                        .stream()
                        .map(MultitrackerController.this::toTracker);
            }
        };
    }

    private Tracker toTracker(URI uri){
        return switch (uri.getScheme()){
            case "http" -> new HttpTracker(uri, this.torrent);
            case "udp" -> throw new UnsupportedOperationException("Not yet implemented");
            default -> throw new IllegalStateException("Unexpected value: " + uri.getScheme());
        };
    }
}
