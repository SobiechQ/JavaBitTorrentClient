package MessageFactory.Model;

import lombok.Getter;

@Getter
public enum DefaultMessage {
    KEEP_ALIVE(MessageType.KEEP_ALIVE), CHOKE(MessageType.CHOKE), UNCHOKE(MessageType.UNCHOKE), INTERESTED(MessageType.INTERESTED), NOT_INTERESTED(MessageType.NOT_INTERESTED);
    private final MessageProjection projection;

    DefaultMessage(MessageType messageType) {
        this.projection = new MessageProjection(messageType);
    }
}
