package Peer.Service;

import Peer.Repository.PeerRepositoryImpl;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class PeerStrategyServiceImpl implements PeerStrategyService {
    private final static int MAX_CONCURRENT_CONNECTIONS = 50;
    private final PeerRepositoryImpl peerRepository;
    private final PeerService peerService;

    public PeerStrategyServiceImpl(@NonNull PeerRepositoryImpl peerRepository, @NonNull PeerService peerService) {
        this.peerRepository = peerRepository;
        this.peerService = peerService;
    }
}
