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
    public void updateAttempt(@NonNull Torrent torrent,@NonNull Peer peer) {
        this.getPeerRepositoryRecord(torrent).updateAttempt(peer);
    }

    @Override
    public void updateLastSeen(@NonNull Torrent torrent, @NonNull Peer peer) {
        this.getPeerRepositoryRecord(torrent).updateLastSeen(peer);
    }

    @Override
    public void setBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        this.getPeerRepositoryRecord(torrent).setBitfield(peer, bitfield);
    }

    @Override
    public Stream<PeerStatistic> getPeerStatisticProjection(@NonNull Torrent torrent) {
        return this.getPeerRepositoryRecord(torrent).getPeers();
    }

    @Override
    public void setChoked(@NonNull Torrent torrent, @NonNull Peer peer) {
        this.getPeerRepositoryRecord(torrent).getStatistic(peer).setChoked(true);
    }

    @Override
    public void setUnchoked(@NonNull Torrent torrent,@NonNull Peer peer) {
        this.getPeerRepositoryRecord(torrent).getStatistic(peer).setChoked(false);
    }

    private PeerRepositoryRecord getPeerRepositoryRecord(@NonNull Torrent torrent) {
        return this.peerRepository.computeIfAbsent(torrent, _ -> new PeerRepositoryRecord());
    }
}
