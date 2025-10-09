package Peer.Repository;

import Message.Model.MessageBitfield;
import Peer.Model.Peer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
public class PeerStatistic {
    private final Peer peer;
    @Setter
    @Nullable
    private MessageBitfield bitfield;
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

    public Optional<MessageBitfield> getBitfield() {
        return Optional.ofNullable(bitfield);
    }
}
