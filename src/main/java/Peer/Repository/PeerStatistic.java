package Peer.Repository;

import MessageFactory.Model.MessageBitfield;
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
    private int failedCount;

    //todo delta of downloaded length -

    PeerStatistic(Peer peer) {
        this.peer = peer;
    }

    public boolean isSeeder() {
        return this.getBitfield().map(MessageBitfield::isSeeder).orElse(false);
    }

    public boolean hasPiece(int index) {
        if (isSeeder())
            return true;
        return this.getBitfield().map(mb -> mb.hasPiece(index)).orElse(false);
    }

    public Optional<MessageBitfield> getBitfield() {
        return Optional.ofNullable(bitfield);
    }

    void updateFailed() {
        this.failedCount++;
    }

}
