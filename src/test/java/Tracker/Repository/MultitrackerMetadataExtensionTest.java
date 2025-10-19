package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import java.net.URI;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MultitrackerMetadataExtensionTest {

    private final static List<List<URI>> MOCK_ANNOUNCE_LIST = List.of(
            List.of(URI.create("http://t0e0"), URI.create("http://t0e1"), URI.create("http://t0e2")),
            List.of(URI.create("http://t1e0"), URI.create("http://t1e1")),
            List.of(URI.create("http://t2e0"))
    );

    @Test
    void testGetTrackers() {
//        Torrent torrent = mock(Torrent.class);
//        when(torrent.getAnnounceList()).then((Answer<Stream<List<URI>>>) _ -> MOCK_ANNOUNCE_LIST.stream());
//        when(torrent.isAnnounceListAvailable()).then(_ -> true);
//
//        final var multitracker = new MultitrackerMetadataExtension(torrent);
//
//        Assertions.assertEquals(multitracker.getTrackers(0).count(), 3);
//        Assertions.assertEquals(multitracker.getTrackers(0).count(), 3);
//        Assertions.assertEquals(multitracker.getTrackers(1).count(), 2);
//        Assertions.assertEquals(multitracker.getTrackers(2).count(), 1);
//        Assertions.assertEquals(multitracker.getTrackers(3).count(), 0);
//        Assertions.assertEquals(multitracker.getLayersCount(), 3);
    }

    @Test
    void testNotifySuccess() {
//        Torrent torrent = mock(Torrent.class);
//        when(torrent.getAnnounceList()).then((Answer<Stream<List<URI>>>) _ -> MOCK_ANNOUNCE_LIST.stream());
//        when(torrent.isAnnounceListAvailable()).then(_ -> true);
//
//        final var multitracker = new MultitrackerMetadataExtension(torrent);
//        final var t0e0 = multitracker.getTrackers(0).toList().get(0);
//        final var t0e1 = multitracker.getTrackers(0).toList().get(1);
//        final var t0e2 = multitracker.getTrackers(0).toList().get(2);
//
//        multitracker.notifySuccess(t0e2);
//
//        final var listAfterNotify = multitracker.getTrackers(0).toList();
//
//        Assertions.assertEquals(listAfterNotify.get(0), t0e2);
//        Assertions.assertEquals(listAfterNotify.get(1), t0e0);
//        Assertions.assertEquals(listAfterNotify.get(2), t0e1);
//        Assertions.assertEquals(multitracker.getLayersCount(), 3);
    }

    @Test
    void testGetTrackersNoList() {
//        Torrent torrent = mock(Torrent.class);
//        when(torrent.getAnnounce()).then(_ -> URI.create("http://t0e0"));
////        when(torrent.isAnnounceListAvailable()).then(_ -> false);
//
//        final var multitracker = new MultitrackerMetadataExtension(torrent);
//
//        Assertions.assertEquals(multitracker.getTrackers(0).count(), 1);
//        Assertions.assertEquals(multitracker.getTrackers(1).count(), 0);
//        Assertions.assertEquals(multitracker.getLayersCount(), 1);
    }


}