package Peer.Service;

import Message.Model.MessageRequest;
import Model.DecodedBencode.Torrent;
import Peer.Model.PeerMessage;
import Peer.Model.PeerStatistic;
import Peer.Repository.PeerRepository;
import Tracker.Controller.TrackerController;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.ToIntFunction;

import static Message.Model.DefaultMessage.CHOKE;
import static Message.Model.DefaultMessage.UNCHOKE;

@Service
public class PeerServiceImpl implements PeerService {

    private TrackerController trackerController;
    private Map<Torrent, PeerRepository> repositoryMap;

    public PeerServiceImpl(TrackerController trackerController) {
        this.trackerController = trackerController;
        this.repositoryMap = new HashMap<>();
    }

    public MessageRequest getRequest(@NonNull Torrent torrent) {
        return null;
    }

    @Override
    public List<PeerMessage> chokeAlgorithm(Torrent torrent) {
        final var split = this.getPeerRepository(torrent)
                .getPeers()
                .sorted(Comparator.comparingInt(PeerStatistic::getUnchokedCount))
                .splitAt(4);

        final var unchoke = split.v1.map(p -> new PeerMessage(p.getPeer(), UNCHOKE.getProjection()));
        final var choke = split.v2.map(p -> new PeerMessage(p.getPeer(), CHOKE.getProjection()));

        return Seq.concat(unchoke, choke).toList();
    }

    @Override
    public List<PeerMessage> optimisticUnchoke(Torrent torrent) {
        return List.of();
    }

    private PeerRepository getPeerRepository(@NonNull Torrent torrent) {
        return this.repositoryMap.computeIfAbsent(torrent, _ -> new PeerRepository());
    }
}
