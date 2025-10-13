package Peer.Service;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerMessageProjection;
import Peer.Repository.PeerRepository;
import Peer.Repository.PeerStatistic;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

import static Message.Model.DefaultMessage.CHOKE;
import static Message.Model.DefaultMessage.UNCHOKE;

@Service
public class PeerStrategyServiceImpl implements PeerStrategyService {
    private final static int MAX_CONCURRENT_CONNECTIONS = 50;
    private final static long MAX_TIMEOUT = 1000 * 60 * 30;

    private final PeerRepository peerRepository;

    public PeerStrategyServiceImpl(@NonNull PeerRepository peerRepository) {
        this.peerRepository = peerRepository;
    }

    @Override
    public Stream<Peer> getPeers(@NonNull Torrent torrent) {
        return this.peerRepository.getPeerStatisticProjection(torrent)
                .filter(PeerStatistic::isUnchoked)
                .map(PeerStatistic::getPeer)
                .limit(MAX_CONCURRENT_CONNECTIONS);
    }

    @Override
    public Stream<Peer> getPeers(Torrent torrent, int index) {
        return this.peerRepository.getPeerStatisticProjection(torrent)
                .filter(PeerStatistic::isUnchoked)
                .filter(ps -> ps.hasPiece(index))
                .map(PeerStatistic::getPeer)
                .limit(MAX_CONCURRENT_CONNECTIONS);
    }

    @Override
    public Stream<PeerMessageProjection> chokingAndUnchoking(Torrent torrent) {
        final var peerStatistic = Seq.ofType(this.peerRepository.getPeerStatisticProjection(torrent), PeerStatistic.class);

        final var split = peerStatistic
                .shuffle()
                .splitAt(5);

        final var unchoke = split.v1
                .map(PeerStatistic::getPeer)
                .peek(p -> peerRepository.setUnchoked(torrent, p))
                .map(p -> new PeerMessageProjection(p, CHOKE.getProjection()));

        final var choke = split.v2
                .map(PeerStatistic::getPeer)
                .peek(p -> peerRepository.setChoked(torrent, p))
                .map(p -> new PeerMessageProjection(p, CHOKE.getProjection()));

        return Seq.concat(unchoke, choke);
    }

    @Override
    public Stream<PeerMessageProjection> optimisticUnchoke(Torrent torrent) {
        return Seq.ofType(this.peerRepository.getPeerStatisticProjection(torrent), PeerStatistic.class)
                .filter(PeerStatistic::isChoked)
                .map(PeerStatistic::getPeer)
                .shuffle()
                .limit(5)
                .peek(p -> peerRepository.setUnchoked(torrent, p))
                .map(p -> new PeerMessageProjection(p, UNCHOKE.getProjection()));
    }

    @Override
    public Stream<PeerMessageProjection> chokeUnreachable(Torrent torrent) {
        return this.peerRepository.getPeerStatisticProjection(torrent)
                .filter(ps -> System.currentTimeMillis() - ps.getLastSeen() >= MAX_TIMEOUT)
                .map(PeerStatistic::getPeer)
                .peek(p -> peerRepository.setUnchoked(torrent, p))
                .map(p -> new PeerMessageProjection(p, UNCHOKE.getProjection()));
    }
}
