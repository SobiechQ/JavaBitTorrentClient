package Peer.Service;

import Message.Model.MessageBitfield;
import Message.Service.MessageService;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerMessageProjection;
import Peer.Repository.PeerStatistic;
import Peer.Repository.PeerRepository;
import Tracker.Controller.TrackerController;
import Utils.ByteUtils;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

import static Message.Model.DefaultMessage.CHOKE;
import static Message.Model.DefaultMessage.UNCHOKE;

@Service
public class PeerServiceImpl implements PeerService {

    private final static byte[] PEER_ID;

    private final TrackerController trackerController;
    private final Map<Torrent, PeerRepository> peerRepository;

    static {
        PEER_ID = ByteUtils.getRandomByteArray(20);
    }

    public PeerServiceImpl(TrackerController trackerController) {
        this.trackerController = trackerController;
        this.peerRepository = new HashMap<>();
    }

    @Override
    public List<PeerMessageProjection> chokeAlgorithm(Torrent torrent) {
        final var peerStatistic = Seq.ofType(this.getPeerRepository(torrent).getPeers(), PeerStatistic.class);

        final var split = peerStatistic
                .sorted(Comparator.comparingInt(PeerStatistic::getUnchokedCount))
                .splitAt(4);

        final var unchoke = split.v1.map(p -> new PeerMessageProjection(p.getPeer(), UNCHOKE.getProjection()));
        final var choke = split.v2.map(p -> new PeerMessageProjection(p.getPeer(), CHOKE.getProjection()));

        return Seq.concat(unchoke, choke).toList();
    }

    @Override
    public List<PeerMessageProjection> optimisticUnchoke(Torrent torrent) {
        return List.of();
    }

    private void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.getPeerRepository(torrent).setBitfield(peer, bitfield);
    }

    private PeerRepository getPeerRepository(@NonNull Torrent torrent) {
        return this.peerRepository.computeIfAbsent(torrent, _ -> new PeerRepository());
    }
}
