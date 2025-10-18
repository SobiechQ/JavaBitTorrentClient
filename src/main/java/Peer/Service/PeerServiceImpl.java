package Peer.Service;

import MessageFactory.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Repository.PeerRepository;
import Tracker.Controller.TrackerController;
import Tracker.Model.Messages.TrackerResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PeerServiceImpl implements PeerService {
    private static final Logger log = LoggerFactory.getLogger(PeerServiceImpl.class);
    private final TrackerController trackerController;
    private final PeerRepository peerRepository;

    public PeerServiceImpl(@NonNull TrackerController trackerController, @NonNull PeerRepository peerRepository) {
        this.trackerController = trackerController;
        this.peerRepository = peerRepository;
    }

    @Override
    public void announce(@NonNull Torrent torrent) {
        trackerController.subscribeAnnounce(torrent, this::handleTrackerResponse);
    }

    @Override
    public void subscribeAsyncRevalidation(@NonNull Torrent torrent) {
        this.trackerController.subscribeAsyncRevalidation(torrent, this::handleTrackerResponse);
    }

    @Override
    public void notifyAttempt(Torrent torrent, Peer peer) {
        this.peerRepository.updateAttempt(torrent, peer);
    }

    @Override
    public void notifySuccess(@NonNull Torrent torrent, @NonNull Peer peer) {
        this.peerRepository.updateLastSeen(torrent, peer);
    }

    @Override
    public void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.peerRepository.setBitfield(torrent, peer, bitfield);
    }

    @Override
    public void handleChoke(@NonNull Torrent torrent, @NonNull Peer peer) {
        //todo implement
    }

    @Override
    public void handleUnchoke(@NonNull Torrent torrent, @NonNull Peer peer) {
        //todo implement
    }

    private void handleTrackerResponse(@NonNull TrackerResponse response) {
        log.info("Handling tracker response {}", response);
        final var torrent = response.getRespondTo().getTorrent();
        response.getPeers()
                .forEach(p -> peerRepository.addPeer(torrent, p));

    }
}
