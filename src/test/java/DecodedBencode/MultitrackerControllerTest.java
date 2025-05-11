package DecodedBencode;

import Tracker.MultitrackerController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

public class MultitrackerControllerTest {



    @Test
    void testGetAnnounce() throws IOException {
        final var location = "src/test/java/resources/mock_torrent.torrent";
        final var torrent = Torrent.fromFile(new File(location));
        final var controller = new MultitrackerController(torrent);

        Assertions.assertDoesNotThrow(() ->{
            for (Stream<URI> _ : controller) {

            }
        });
        final Stream.Builder<List<URI>> builder = Stream.builder();
        builder.add(List.of(torrent.getAnnounce()));
        torrent.getAnnounceList().forEach(builder::add);

        final var announcersTorrent = builder.build().toList();
        final var announcersController = controller.stream().map(Stream::toList).toList();


        System.out.println(announcersTorrent);

        Assertions.assertEquals(announcersTorrent, announcersController);
    }
}
