package TCP;

import DecodedBencode.Torrent;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * https://www.bittorrent.org/beps/bep_0012.html
 */
public class MultitrackerController implements Iterable<Stream<String>> {
    private final Torrent torrent;

    public MultitrackerController(@NonNull Torrent torrent) {
        this.torrent = torrent;
    }

    public Stream<Stream<String>> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator(), Spliterator.ORDERED), false);
    }

    @NotNull
    @Override
    public Iterator<Stream<String>> iterator() {
        return new Iterator<>() {
            private int tier = 0;

            @Override
            public boolean hasNext() {
                return torrent.getAnnounceList()
                               .count() >= tier;
            }

            @Override
            public Stream<String> next() {
                this.tier++;

                if (this.tier - 1 == 0)
                    return Stream.of(torrent.getAnnounce());

                return torrent.getAnnounceList()
                        .skip(tier - 2)
                        .findFirst()
                        .orElseThrow(NoSuchElementException::new)
                        .stream();
            }
        };
    }
}
