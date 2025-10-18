package Peer.Repository;

import MessageFactory.Model.MessageBitfield;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import lombok.NonNull;
import org.springframework.stereotype.Repository;

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
    public void setBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.getPeerRepositoryRecord(torrent).setBitfield(peer, bitfield);
    }

    @Override
    public Stream<PeerStatistic> getPeerStatisticProjection(@NonNull Torrent torrent) {
        return this.getPeerRepositoryRecord(torrent).getPeers();
    }

    private PeerRepositoryRecord getPeerRepositoryRecord(@NonNull Torrent torrent) {
        return this.peerRepository.computeIfAbsent(torrent, _ -> new PeerRepositoryRecord());
    }
}
