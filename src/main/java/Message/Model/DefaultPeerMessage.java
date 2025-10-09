package Message.Model;

import lombok.Getter;

@Getter
public enum DefaultPeerMessage {
    KEEP_ALIVE(MessageType.KEEP_ALIVE), CHOKE(MessageType.CHOKE), UNCHOKE(MessageType.UNCHOKE), INTERESTED(MessageType.INTERESTED), NOT_INTERESTED(MessageType.NOT_INTERESTED);
    private final PeerMessageProjection projection;

    DefaultPeerMessage(MessageType messageType) {
        this.projection = new PeerMessageProjection(messageType);
    }
}
