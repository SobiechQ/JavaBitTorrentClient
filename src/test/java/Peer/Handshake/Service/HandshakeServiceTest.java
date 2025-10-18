package Peer.Handshake.Service;

import Handshake.Model.HandshakeInputProjection;
import Handshake.Service.HandshakeService;
import Handshake.Service.HandshakeServiceImpl;
import Utils.ByteUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.stream.Stream;

@SpringBootTest(classes = Configuration.Main.class)
@ContextConfiguration(classes = HandshakeServiceImpl.class)
class HandshakeServiceTest {
    private final static byte[] MOCK_INFOHASH;
    private final static byte[] MOCK_PEERID;
    private final static byte[] EMPTY_PEERID = new byte[20];

    static {
        MOCK_INFOHASH = ByteUtils.getRandomByteArray(20);
        MOCK_PEERID = ByteUtils.getRandomByteArray(20);
    }

    @Autowired
    private HandshakeService service;

    @Test
    void getHandshake() {
//        final var handshake = service.getHandshake(MOCK_INFOHASH, MOCK_PEERID);
//        Assertions.assertEquals(handshake.handshake().length, 68);
//        Assertions.assertEquals(handshake.handshake()[0], 19);
//        final var verifySelf = service.verifyHandshake(new HandshakeInputProjection(handshake.handshake()), handshake);
//        Assertions.assertTrue(verifySelf);
    }

    @Test
    void verifyHandshake() {
//        final var handshakeOutput = service.getHandshake(MOCK_INFOHASH, MOCK_PEERID);
//        final var handshakeInput = new HandshakeInputProjection(service.getHandshake(MOCK_INFOHASH, EMPTY_PEERID).handshake());
//
//        Assertions.assertTrue(service.verifyHandshake(handshakeInput, handshakeOutput));
    }
}