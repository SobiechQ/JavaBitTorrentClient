package Peer.Model;

import Message.Model.DefaultPeerMessage;
import Message.Model.MessageType;
import Message.Model.PeerMessageProjection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultPeerMessageTest {

    @Test
    public void equalTest() {
        final var testEnum = DefaultPeerMessage.KEEP_ALIVE.getProjection();
        final var testManual = new PeerMessageProjection(MessageType.KEEP_ALIVE);

        Assertions.assertEquals(testManual, testEnum);
    }

}