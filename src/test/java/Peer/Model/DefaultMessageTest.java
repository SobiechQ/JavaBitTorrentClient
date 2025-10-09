package Peer.Model;

import Message.Model.DefaultMessage;
import Message.Model.MessageType;
import Message.Model.MessageProjection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultMessageTest {

    @Test
    public void equalTest() {
        final var testEnum = DefaultMessage.KEEP_ALIVE.getProjection();
        final var testManual = new MessageProjection(MessageType.KEEP_ALIVE);

        Assertions.assertEquals(testManual, testEnum);
    }

}