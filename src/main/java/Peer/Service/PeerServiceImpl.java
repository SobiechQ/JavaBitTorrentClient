package Peer.Service;

import Handshake.Model.HandshakeInputProjection;
import Handshake.Model.HandshakeOutputProjection;
import Handshake.Service.HandshakeService;
import Message.Model.MessageBitfield;
import Message.Model.MessageRequest;
import Message.Service.MessageService;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerDataInputProjection;
import Peer.Model.PeerMessageProjection;
import Peer.Repository.PeerStatistic;
import Peer.Repository.PeerRepository;
import Peer.Repository.PieceRepository;
import Tracker.Controller.TrackerController;
import Utils.ByteUtils;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import static Message.Model.DefaultMessage.CHOKE;
import static Message.Model.DefaultMessage.UNCHOKE;

@Service
public class PeerServiceImpl implements PeerService {

    private final static byte[] PEER_ID;

    private TrackerController trackerController;
    private final MessageService messageService;
    private Map<Torrent, PeerRepository> peerRepository;
    private Map<Torrent, PieceRepository> pieceRepository;

    static {
        PEER_ID = ByteUtils.getRandomByteArray(20);
    }

    public PeerServiceImpl(TrackerController trackerController, MessageService messageService) {
        this.trackerController = trackerController;
        this.messageService = messageService;
        this.peerRepository = new HashMap<>();
        this.pieceRepository = new HashMap<>();
    }

    @Override
    public void handleInput(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull PeerDataInputProjection inputProjection) {
        final var messageProjection = messageService.decode(inputProjection);

        if (messageProjection instanceof MessageBitfield bitfield) {
            this.handleBitfield(torrent, peer, bitfield);
            return;
        }
    }

    public Stream<PeerMessageProjection> getMessages(@NonNull Torrent torrent) {
        return this.getRequest(torrent);
    }

    private Stream<PeerMessageProjection> getRequest(@NonNull Torrent torrent) {
        return Seq.ofType(this.choosePieces(torrent), Integer.class)
                .innerJoin(this.choosePiers(torrent), (_, _) -> true)
                .map(t -> t.map1(i -> getMessageRequest(torrent, i)))
                .map(t -> new PeerMessageProjection(t.v2, t.v1))
                .limit(5)
                .stream();
    }

    private MessageRequest getMessageRequest(@NonNull Torrent torrent, int index) {
        return messageService.request(index, this.getPieceRepository(torrent).getNextBegin(index));
    }

    //    @Override
    private PeerMessageProjection getBitfield(@NonNull Torrent torrent) {
        return null;
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

    private Stream<Integer> choosePieces(@NonNull Torrent torrent) {
        final var repository = this.getPieceRepository(torrent);

        final var incomplete = repository.getIncompletePieces();
        final var notStarted = repository.getNotStartedPieces();

        return Seq.concat(incomplete, notStarted)
                .stream();
    }

    private Stream<Peer> choosePiers(@NonNull Torrent torrent) {
        return this.getPeerRepository(torrent)
                .getUnchokedPeers();
    }

    private void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.getPeerRepository(torrent).setBitfield(peer, bitfield);
    }

    private PeerRepository getPeerRepository(@NonNull Torrent torrent) {
        return this.peerRepository.computeIfAbsent(torrent, _ -> new PeerRepository());
    }

    private PieceRepository getPieceRepository(@NonNull Torrent torrent) {
        return this.pieceRepository.computeIfAbsent(torrent, PieceRepository::new);
    }
}
