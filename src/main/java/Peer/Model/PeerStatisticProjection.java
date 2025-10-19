package Peer.Model;

import Model.Message.MessageBitfield;
import lombok.Builder;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.Optional;

@Builder
public record PeerStatisticProjection(
        @NonNull Peer peer,
        @Nullable BitSet messageBitfield,
        int failedCount,
        boolean isSeeder) {

    public Optional<BitSet> getBitfield() {
        return Optional.ofNullable(this.messageBitfield);
    }

    public boolean hasPiece(int index) {
        if (isSeeder())
            return true;
        return this.getBitfield().map(bs -> bs.get(index)).orElse(false);
    }

}
