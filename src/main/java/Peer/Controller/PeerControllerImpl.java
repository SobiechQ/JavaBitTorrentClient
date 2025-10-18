package Peer.Controller;

import MessageFactory.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerMessageProjection;
import Peer.Service.PeerService;
import Peer.Service.PeerStrategyService;
import lombok.NonNull;
import org.springframework.stereotype.Controller;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;

@Controller
public class PeerControllerImpl implements PeerController {
    private final PeerService peerService;
    private final PeerStrategyService peerStrategyService;
    private final ScheduledExecutorService scheduledExecutor;

    public PeerControllerImpl(@NonNull PeerService peerService, @NonNull PeerStrategyService peerStrategyService, @NonNull ScheduledExecutorService scheduledExecutor) {
        this.peerService = peerService;
        this.peerStrategyService = peerStrategyService;
        this.scheduledExecutor = scheduledExecutor;
    }

    @Override
    public void announce(@NonNull Torrent torrent, Consumer<Stream<PeerMessageProjection>> consumer) {
        scheduledExecutor.scheduleAtFixedRate(() -> consumer.accept(peerStrategyService.chokingAndUnchoking(torrent)), 30, 30, SECONDS);
        scheduledExecutor.scheduleAtFixedRate(() -> consumer.accept(peerStrategyService.optimisticUnchoke(torrent)), 10, 10, SECONDS);
        scheduledExecutor.scheduleAtFixedRate(() -> consumer.accept(peerStrategyService.chokeUnreachable(torrent)), 20, 20, SECONDS);
        peerService.subscribeAsyncRevalidation(torrent);
    }

    @Override
    public Stream<Peer> getPeers(@NonNull Torrent torrent) {
        return peerStrategyService.getPeers(torrent);
    }

    @Override
    public Stream<Peer> getPeers(@NonNull Torrent torrent, int index) {
        return peerStrategyService.getPeers(torrent, index);
    }

    @Override
    public void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        peerService.handleBitfield(torrent, peer, bitfield);
    }

    @Override
    public void notifyAttempt(@NonNull Torrent torrent, @NonNull Peer peer) {
        peerService.notifyAttempt(torrent, peer);
    }

    @Override
    public void notifySuccess(@NonNull Torrent torrent, @NonNull Peer peer) {
        peerService.notifySuccess(torrent, peer);
    }
}
