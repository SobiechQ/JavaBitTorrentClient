package Tracker.Repository;

import Model.DecodedBencode.Torrent;
import Tracker.Model.HttpTracker;
import Tracker.Model.Tracker;
import Tracker.Model.UdpTracker;
import lombok.Getter;
import lombok.NonNull;
import org.jooq.lambda.Seq;

import java.net.URI;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * <a href="https://www.bittorrent.org/beps/bep_0012.html">Multitracker Metadata Extension</a>
 */
public class MultitrackerMetadataExtension {
    private static class Tier {
        @Getter
        private final int layer;
        private final List<Tracker> trackers;

        private Tier(List<Tracker> trackers, int layer) {
            this.trackers = new LinkedList<>(trackers);
            this.layer = layer;
        }

        private Tier(Tracker tracker, int layer) {
            this(List.of(tracker), layer);
        }

        private void notifySuccess(@NonNull Tracker tracker) {
            if (trackers.remove(tracker))
                trackers.addFirst(tracker);
        }

        private Stream<Tracker> getTrackers() {
            return this.trackers.stream();
        }

    }

    private final Map<Tracker, Tier> trackerToTier;
    private final Map<Integer, Tier> layerToTier;

    public MultitrackerMetadataExtension(@NonNull Torrent torrent) {
        this.trackerToTier = newTrackerToTierMap(torrent);
        this.layerToTier = newLayerToTierMap(this.trackerToTier);
    }

    public void notifySuccess(@NonNull Tracker tracker) {
        this.getTier(tracker)
                .ifPresent(t -> t.notifySuccess(tracker));
    }

    public int getLayersCount() {
        return this.layerToTier.size();
    }

    public Stream<Tracker> getTrackers(int layer) {
        return this.getTier(layer)
                .stream()
                .flatMap(Tier::getTrackers);
    }

    private Optional<Tier> getTier(@NonNull Tracker tracker) {
        return Optional.ofNullable(this.trackerToTier.get(tracker));
    }

    private Optional<Tier> getTier(int layer) {
        return Optional.ofNullable(this.layerToTier.get(layer));
    }

    private static Tracker toTracker(@NonNull URI uri, @NonNull Torrent torrent) {
        return switch (uri.getScheme()) {
            case "http", "https" -> new HttpTracker(uri, torrent);
            case "udp" -> new UdpTracker(uri, torrent);
            default -> throw new IllegalStateException("Unexpected value: " + uri.getScheme());
        };
    }

    private static Map<Tracker, Tier> newTrackerToTierMap(@NonNull Torrent torrent) {
        if (!torrent.isAnnounceListAvailable()) {
            final var tracker = toTracker(torrent.getAnnounce(), torrent);
            return Map.of(tracker, new Tier(tracker, 0));
        }

        //noinspection unchecked
        return Seq.ofType(torrent.getAnnounceList(), List.class)
                .map(l -> (List<URI>) l)
                .zipWithIndex()
                .map(t -> t.map2(Math::toIntExact))
                .map(t -> t.map1(l -> l.stream().map(u -> toTracker(u, torrent)).toList()))
                .map(t -> new Tier(t.v1, t.v2))
                .collect((Supplier<Map<Tracker, Tier>>) HashMap::new,
                        (trackerTierMap, tier) -> tier.getTrackers().forEach(t -> trackerTierMap.put(t, tier)),
                        Map::putAll);
    }

    private static Map<Integer, Tier> newLayerToTierMap(Map<Tracker, Tier> trackerToTier) {
        return Seq.ofType(trackerToTier.values().stream(), Tier.class)
                .collect(TreeMap::new, (integerTierMap, tier) -> integerTierMap.putIfAbsent(tier.getLayer(), tier), (BiConsumer<Map<Integer, Tier>, Map<Integer, Tier>>) Map::putAll);
    }
}
