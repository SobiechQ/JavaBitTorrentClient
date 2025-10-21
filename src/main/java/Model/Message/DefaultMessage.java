package Model.Message;

import lombok.Getter;

@Getter
public enum DefaultMessage {
    KEEP_ALIVE(new MessageKeepAlive()), CHOKE(new MessageChoke()), UNCHOKE(new MessageUnchoke()), INTERESTED(new MessageInterested()), NOT_INTERESTED(new MessageNotInterested()), CANCEL(new MessageCancel()), PORT(new MessagePort());
    private final MessageProjection projection;

    DefaultMessage(MessageProjection projection) {
        this.projection = projection;
    }
}
