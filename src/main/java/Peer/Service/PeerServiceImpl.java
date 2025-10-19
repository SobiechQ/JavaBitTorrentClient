package Peer.Service;

import Model.Message.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Repository.PeerRepository;
import Tracker.Model.Messages.TrackerResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PeerServiceImpl implements PeerService {
    private static final Logger log = LoggerFactory.getLogger(PeerServiceImpl.class);
    private final PeerRepository peerRepository;

    @Override
    public void notifyFailed(@NonNull Torrent torrent,@NonNull Peer peer) {
        this.peerRepository.updateFailed(torrent, peer);
    }

    @Override
    public void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.peerRepository.setBitfield(torrent, peer, bitfield);
    }

    private void handleTrackerResponse(@NonNull TrackerResponse response) {
        log.info("Handling tracker response {}", response);
        final var torrent = response.getRespondTo().getTorrent();
        response.getPeers()
                .forEach(p -> peerRepository.addPeer(torrent, p));

    }
}
