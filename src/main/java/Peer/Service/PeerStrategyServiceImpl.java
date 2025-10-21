package Peer.Service;

import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerMessageProjection;
import Peer.Model.PeerStatisticProjection;
import Peer.Repository.PeerRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class PeerStrategyServiceImpl implements PeerStrategyService {
    private final PeerRepository peerRepository;

    public PeerStrategyServiceImpl(@NonNull PeerRepository peerRepository) {
        this.peerRepository = peerRepository;
    }

    @Override
    public Stream<Peer> getPeers(@NonNull Torrent torrent) {
        return this.peerRepository.getPeerStatisticProjection(torrent)
                .sorted(Comparator.comparingInt(PeerStatisticProjection::failedCount))
                .map(PeerStatisticProjection::peer);
    }

    @Override
    public Stream<Peer> getPeers(Torrent torrent, int index) {
        return this.peerRepository.getPeerStatisticProjection(torrent)
                .filter(ps -> ps.hasPiece(index))
                .sorted(Comparator.comparingInt(PeerStatisticProjection::failedCount))
                .map(PeerStatisticProjection::peer);
    }

    @Override
    public Stream<Integer> getPiecesRarest(@NonNull Torrent torrent) {
        return this.peerRepository
                .getPeerStatisticProjection(torrent)
                .map(PeerStatisticProjection::getBitfield)
                .flatMap(Optional::stream)
                .flatMap(bs -> bs.stream().boxed())
                .collect(Collectors.groupingBy(i -> i, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    @Override
    public Stream<PeerMessageProjection> chokingAndUnchoking(Torrent torrent) {
//        log.info("Choking and unchoking algorithm started");
//        final var peerStatistic = Seq.ofType(this.peerRepository.getPeerStatisticProjection(torrent), PeerStatistic.class);
//
//        final var split = peerStatistic
//                .shuffle()
//                .splitAt(5);
//
//        final var unchoke = split.v1
//                .map(PeerStatistic::getPeer)
//                .peek(p -> peerRepository.setUnchoked(torrent, p))
//                .map(p -> new PeerMessageProjection(p, CHOKE.getProjection()));
//
//        final var choke = split.v2
//                .map(PeerStatistic::getPeer)
//                .peek(p -> peerRepository.setChoked(torrent, p))
//                .map(p -> new PeerMessageProjection(p, CHOKE.getProjection()));

        return Seq.of();
    }

    @Override
    public Stream<PeerMessageProjection> optimisticUnchoke(Torrent torrent) {
//        log.info("Optimistic unchoke algorithm started");
//        return Seq.ofType(this.peerRepository.getPeerStatisticProjection(torrent), PeerStatistic.class)
//                .filter(PeerStatistic::isChoked)
//                .map(PeerStatistic::getPeer)
//                .shuffle()
//                .limit(5)
//                .peek(p -> peerRepository.setUnchoked(torrent, p))
//                .map(p -> new PeerMessageProjection(p, UNCHOKE.getProjection()));

        return Seq.of();
    }

    @Override
    public Stream<PeerMessageProjection> chokeUnreachable(Torrent torrent) {
//        log.info("Choke unreachable algorithm started");
//        return this.peerRepository.getPeerStatisticProjection(torrent)
//                .filter(ps -> System.currentTimeMillis() - ps.getLastSeen() >= MAX_TIMEOUT)
//                .map(PeerStatistic::getPeer)
//                .peek(p -> peerRepository.setUnchoked(torrent, p))
//                .map(p -> new PeerMessageProjection(p, UNCHOKE.getProjection()));
        return Seq.of();
    }
}
