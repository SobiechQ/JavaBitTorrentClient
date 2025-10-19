package Peer.Repository;

import Model.Message.MessageBitfield;
import Peer.Model.Peer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Optional;

@Getter
class PeerStatistic {
    private final Peer peer;
    @Setter
    @Nullable
    private BitSet bitfield;
    private int failedCount;

    //todo delta of downloaded length -

    PeerStatistic(Peer peer) {
        this.peer = peer;
    }

    boolean isSeeder() {
        return this.getBitfield()
                .map(bs -> bs.nextClearBit(0) >= bs.length())
                .orElse(false);
    }

    boolean hasPiece(int index) {
        if (isSeeder())
            return true;
        return this.getBitfield().map(bs -> bs.get(index)).orElse(false);
    }

    Optional<BitSet> getBitfield() {
        return Optional.ofNullable(bitfield);
    }

    void updateFailed() {
        this.failedCount++;
    }

    void updateBitfield(int index) {
        if (this.bitfield == null)
            this.bitfield = new BitSet();
        this.bitfield.set(index);
    }

}
