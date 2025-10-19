package Peer.Model;

import MessageFactory.Model.MessageBitfield;
import lombok.Builder;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Builder
public record PeerStatisticProjection(
        @NonNull Peer peer,
        @Nullable MessageBitfield messageBitfield,
        int failedCount,
        boolean isSeeder) {

    public Optional<MessageBitfield> getBitfield() {
        return Optional.ofNullable(this.messageBitfield);
    }

    public boolean hasPiece(int index) {
        if (isSeeder())
            return true;
        return this.getBitfield().map(mb -> mb.hasPiece(index)).orElse(false);
    }

}
