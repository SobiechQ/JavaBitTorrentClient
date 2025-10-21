package Peer.Service;

import Model.Message.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Model.Message.MessageHave;
import Peer.Model.Peer;
import Peer.Repository.PeerRepository;
import Tracker.Model.Messages.TrackerResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.BitSet;

@Service
@AllArgsConstructor
public class PeerServiceImpl implements PeerService {
    private final PeerRepository peerRepository;

    @Override
    public void notifyFailed(@NonNull Torrent torrent, @NonNull Peer peer) {
        this.peerRepository.updateFailed(torrent, peer);
    }

    @Override
    public void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.peerRepository.setBitfield(torrent, peer, bitfield.getBitfield());
    }

    @Override
    public void handleHave(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageHave have) {

    }

    @Override
    public boolean isPieceAvailable(@NonNull Torrent torrent, @NonNull Peer peer, int index) {
        return this.peerRepository
                .getPeerStatisticProjection(torrent, peer)
                .hasPiece(index);
    }
}
