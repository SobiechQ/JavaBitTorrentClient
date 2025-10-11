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
    private long lastSeen;

    @Setter
    private boolean isChoked = true;

    public PeerStatistic(Peer peer) {
        this.lastSeen = System.currentTimeMillis();
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

    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    public boolean isUnchoked() {
        return !this.isChoked;
    }

    public Optional<MessageBitfield> getBitfield() {
        return Optional.ofNullable(bitfield);
    }
}
