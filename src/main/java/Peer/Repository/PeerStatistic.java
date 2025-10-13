package Peer.Repository;

import Message.Model.MessageBitfield;
import Peer.Model.Peer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@Getter
class PeerStatistic {
    private final Peer peer;
    @Setter
    @Nullable
    private MessageBitfield bitfield;
    private long lastSeen;
    private long lastAttempt;

    @Setter
    private boolean isChoked = true;

    PeerStatistic(Peer peer) {
        this.lastSeen = System.currentTimeMillis();
        this.lastAttempt = System.currentTimeMillis();
        this.peer = peer;
    }

    boolean isSeeder() {
        return this.getBitfield().map(MessageBitfield::isSeeder).orElse(false);
    }

    boolean hasPiece(int index) {
        if (isSeeder())
            return true;
        return this.getBitfield().map(mb -> mb.hasPiece(index)).orElse(false);
    }

    void updateLastSeen() {
        final var time = System.currentTimeMillis();

        this.lastAttempt = time;
        this.lastSeen = time;
    }

    void updateLastAttempt() {
        this.lastAttempt = System.currentTimeMillis();
    }

    boolean isUnchoked() {
        return !this.isChoked;
    }

    Optional<MessageBitfield> getBitfield() {
        return Optional.ofNullable(bitfield);
    }
}
