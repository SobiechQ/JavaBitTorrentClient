package Peer.Model;

import MessageFactory.Model.DefaultMessage;
import MessageFactory.Model.MessageType;
import MessageFactory.Model.MessageProjection;
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