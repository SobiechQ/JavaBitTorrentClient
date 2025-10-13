package Peer.Repository;

import Message.Model.MessageBitfield;
import Peer.Model.Peer;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

@Getter
public class PeerStatistic {
    private final Peer peer;
    @Setter
    @Nullable
    private MessageBitfield bitfield;
    private long lastSeen;
    private long lastAttempt;

    //todo delta of downloaded length -

    @Setter
    private boolean isChoked = true;

    PeerStatistic(Peer peer) {
        this.lastSeen = System.currentTimeMillis();
        this.lastAttempt = System.currentTimeMillis();
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

    public boolean isChoked() {
        return this.isChoked;
    }

    public boolean isUnchoked() {
        return !this.isChoked;
    }

    void updateLastAttempt() {
        this.lastAttempt = System.currentTimeMillis();
    }

    void updateLastSeen() {
        final var time = System.currentTimeMillis();

        this.lastAttempt = time;
        this.lastSeen = time;
    }
}
