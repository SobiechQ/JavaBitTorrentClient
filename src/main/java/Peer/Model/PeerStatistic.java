package Peer.Model;

import lombok.Getter;

@Getter
public class PeerStatistic {
    private final Peer peer;
    private int chokedCount = 0;
    private int unchokedCount = 0;

    public PeerStatistic(Peer peer) {
        this.peer = peer;
    }

    private void addChocked() {
        chokedCount++;
    }

    private void addUnchoked() {
        unchokedCount++;
    }
}
