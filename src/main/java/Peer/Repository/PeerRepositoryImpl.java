package Peer.Repository;

import Model.Message.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import Peer.Model.PeerStatisticProjection;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Repository
public class PeerRepositoryImpl implements PeerRepository {
    private final Map<Torrent, PeerRepositoryRecord> peerRepository;

    public PeerRepositoryImpl() {
        this.peerRepository = new HashMap<>();
    }

    @Override
    public void addPeer(@NonNull Torrent torrent, @NonNull Peer peer) {
        this.getPeerRepositoryRecord(torrent).addPeer(peer);
    }

    @Override
    public void updateFailed(@NonNull Torrent torrent, @NonNull Peer peer) {
        this.getPeerRepositoryRecord(torrent).updateFailed(peer);
    }

    @Override
    public void setBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull BitSet bitfield) {
        this.getPeerRepositoryRecord(torrent).setBitfield(peer, bitfield);
    }

    @Override
    public void updateBitfield(@NonNull Torrent torrent, @NonNull Peer peer, int index) {
        this.getPeerRepositoryRecord(torrent).updateBitfield(peer, index);
    }

    @Override
    public Stream<PeerStatisticProjection> getPeerStatisticProjection(@NonNull Torrent torrent) {
        return this.getPeerRepositoryRecord(torrent)
                .getPeers()
                .stream();
    }

    @Override
    public PeerStatisticProjection getPeerStatisticProjection(@NonNull Torrent torrent, @NonNull Peer peer) {
        return this.getPeerRepositoryRecord(torrent)
                .getStatisticProjection(peer);
    }

    private PeerRepositoryRecord getPeerRepositoryRecord(@NonNull Torrent torrent) {
        return this.peerRepository.computeIfAbsent(torrent, _ -> new PeerRepositoryRecord());
    }
}
