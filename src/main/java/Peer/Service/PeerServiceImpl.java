package Peer.Service;

import Message.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Repository.PeerRepository;
import Tracker.Controller.TrackerController;
import Tracker.Model.Messages.TrackerResponse;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class PeerServiceImpl implements PeerService {
    private final TrackerController trackerController;
    private final PeerRepository peerRepository;

    public PeerServiceImpl(@NonNull TrackerController trackerController,@NonNull PeerRepository peerRepository) {
        this.trackerController = trackerController;
        this.peerRepository = peerRepository;
    }

    @Override
    public void announce(@NonNull Torrent torrent) {
        trackerController.subscribeAnnounce(torrent, this::handleResponse);
    }

    @Override
    public void subscribeAsyncRevalidation(@NonNull Torrent torrent) {
        this.trackerController.subscribeAsyncRevalidation(torrent, this::handleResponse);
    }

    @Override
    public void notifySuccess(@NonNull Torrent torrent, @NonNull Peer peer) {
        this.peerRepository.updateLastSeen(torrent, peer);
    }

    @Override
    public void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.peerRepository.setBitfield(torrent, peer, bitfield);
    }

    private void handleResponse(@NonNull TrackerResponse response) {
        final var torrent = response.getRespondTo().getTorrent();
        response.getPeers()
                .forEach(p -> peerRepository.addPeer(torrent, p));

    }
}
