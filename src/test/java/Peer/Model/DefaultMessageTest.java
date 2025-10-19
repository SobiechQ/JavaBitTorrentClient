package Peer.Model;

import Model.Message.DefaultMessage;
import Model.Message.MessageType;
import Model.Message.MessageProjection;
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