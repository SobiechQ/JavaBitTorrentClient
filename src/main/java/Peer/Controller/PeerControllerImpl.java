package Peer.Controller;

import MessageFactory.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import Peer.Service.PeerStrategyService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Controller;

import java.util.stream.Stream;

@Controller
@AllArgsConstructor
public class PeerControllerImpl implements PeerController {
    private final PeerService peerService;
    private final PeerStrategyService peerStrategyService;

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
}
