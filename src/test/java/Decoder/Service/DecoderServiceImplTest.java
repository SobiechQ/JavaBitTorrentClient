package Decoder.Service;

import Handshake.Service.HandshakeService;
import Handshake.Service.HandshakeServiceImpl;
import Model.Bencode.DecodingError;
import Model.DecodedBencode.Torrent;
import Model.Message.*;
import Utils.ByteUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.stream.Stream;

import static Model.Message.MessageType.*;

@SpringBootTest(classes = Configuration.Main.class)
@ContextConfiguration(classes = {DecoderServiceImpl.class, HandshakeServiceImpl.class})
class DecoderServiceImplTest {
    @Autowired
    private DecoderService decoderService;
    @Autowired
    private HandshakeService handshakeService;

    public final static Torrent MOCK_TORRENT;

    static {
        try {
            MOCK_TORRENT = Torrent.fromFile(new File("src/test/java/resources/debian-12.8.0-amd64-netinst.iso.torrent"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void decodeHandshake() {
        final var handshakeOutput = handshakeService.getHandshake(MOCK_TORRENT);
        final var buffer = ByteBuffer.allocate(4096);
        buffer.put(handshakeOutput.handshake());

        final var handshakeInput = decoderService.decodeHandshake(buffer).orElse(null);

        Assertions.assertNotNull(handshakeInput);
        Assertions.assertArrayEquals(handshakeOutput.handshake(), handshakeInput.handshake());
        Assertions.assertTrue(handshakeService.verifyHandshake(MOCK_TORRENT, handshakeInput));
        Assertions.assertEquals(4028, buffer.remaining());
    }

    @Test
    void decodeHandshakeEmpty() {
        final var handshakeOutput = handshakeService.getHandshake(MOCK_TORRENT);
        final var buffer = ByteBuffer.allocate(4096);
        buffer.put(handshakeOutput.handshake());
        buffer.put(17, (byte) 0);

        final var handshakeInput = decoderService.decodeHandshake(buffer);

        Assertions.assertTrue(handshakeInput.isEmpty());
        Assertions.assertEquals(4096, buffer.remaining());
    }

    static Stream<Arguments> sourceDecodeDefaultMessage() throws DecodingError {
        return Stream.of(
                Arguments.of(new byte[]{0, 0, 0, 1, 0}, CHOKE),
                Arguments.of(new byte[]{0, 0, 0, 1, 1}, UNCHOKE),
                Arguments.of(new byte[]{0, 0, 0, 1, 2}, INTERESTED),
                Arguments.of(new byte[]{0, 0, 0, 1, 3}, NOT_INTERESTED)
        );
    }

    @ParameterizedTest
    @MethodSource("sourceDecodeDefaultMessage")
    void decodeDefaultMessage(byte[] inputArray, MessageType expectedType) {
        final var buffer = ByteBuffer.allocate(4096);
        buffer.put(inputArray);
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);

        Assertions.assertNotNull(decoded);
        Assertions.assertEquals(expectedType, decoded.getMessageType());
        Assertions.assertEquals(4091, buffer.remaining());
    }

    @Test
    void decodeKeepAlive() {
        final var data = DefaultMessage.KEEP_ALIVE.getProjection();
        final var buffer = ByteBuffer.allocate(4096);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);

        Assertions.assertNotNull(decoded);
        Assertions.assertEquals(KEEP_ALIVE, decoded.getMessageType());
        Assertions.assertEquals(4092, buffer.remaining());
    }

    @Test
    void decodeMessageHave() {
        final var data = new MessageHave(4321);
        final var buffer = ByteBuffer.allocate(4096);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);
        Assertions.assertNotNull(decoded);
        final var index = ((MessageHave) decoded).getIndex();

        Assertions.assertEquals(HAVE, decoded.getMessageType());
        Assertions.assertEquals(4321, index);
        Assertions.assertEquals(4087, buffer.remaining());
    }

    @Test
    void decodeMessageHaveMinimal() {
        final var data = new MessageHave(4321);
        final var buffer = ByteBuffer.allocate(9);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);
        Assertions.assertNotNull(decoded);
        final var index = ((MessageHave) decoded).getIndex();

        Assertions.assertEquals(HAVE, decoded.getMessageType());
        Assertions.assertEquals(4321, index);
        Assertions.assertEquals(0, buffer.remaining());
    }

    @Test
    void decodeMessageHaveEmpty() {
        final var data = new MessageHave(4321);
        final var buffer = ByteBuffer.allocate(8);
        buffer.put(data.getData(), 0, 8);
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer);

        Assertions.assertTrue(decoded.isEmpty());
        Assertions.assertEquals(8, buffer.remaining());
    }

    @Test
    void decodeMessageBitfield() {
        final var bytes = new byte[]{(byte) 0xC9, (byte) 0x80};
        final var data = new MessageBitfield(bytes);
        final var buffer = ByteBuffer.allocate(4096);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);
        Assertions.assertNotNull(decoded);

        final var booleans = ByteUtils.bytesToBooleans(bytes);
        for (int i = 0; i < 16; i++) {
            final var hasPiece = ((MessageBitfield) decoded).hasPiece(i);
            Assertions.assertEquals(booleans[i], hasPiece);
        }

        Assertions.assertEquals(BITFIELD, decoded.getMessageType());
        Assertions.assertEquals(4089, buffer.remaining());
    }

    @Test
    void decodeMessageBitfieldMinima() {
        final var bytes = new byte[]{(byte) 0xC9, (byte) 0x80};
        final var data = new MessageBitfield(bytes);
        final var buffer = ByteBuffer.allocate(7);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);
        Assertions.assertNotNull(decoded);

        final var booleans = ByteUtils.bytesToBooleans(bytes);
        for (int i = 0; i < 16; i++) {
            final var hasPiece = ((MessageBitfield) decoded).hasPiece(i);
            Assertions.assertEquals(booleans[i], hasPiece);
        }

        Assertions.assertEquals(BITFIELD, decoded.getMessageType());
        Assertions.assertEquals(0, buffer.remaining());
    }

    @Test
    void decodeMessageBitfieldEmpty() {
        final var bytes = new byte[]{(byte) 0xC9, (byte) 0x80};
        final var data = new MessageBitfield(bytes);
        final var buffer = ByteBuffer.allocate(6);
        buffer.put(data.getData(), 0, 6);
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer);

        Assertions.assertTrue(decoded.isEmpty());
        Assertions.assertEquals(6, buffer.remaining());
    }

    @Test
    void decodeMessageRequest() {
        final var data = new MessageRequest(1111, 2222, 3333);
        final var buffer = ByteBuffer.allocate(4096);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);
        Assertions.assertNotNull(decoded);
        final var index = ((MessageRequest) decoded).getIndex();
        final var begin = ((MessageRequest) decoded).getBegin();
        final var length = ((MessageRequest) decoded).getLength();

        Assertions.assertEquals(REQUEST, decoded.getMessageType());
        Assertions.assertEquals(1111, index);
        Assertions.assertEquals(2222, begin);
        Assertions.assertEquals(3333, length);
        Assertions.assertEquals(4079, buffer.remaining());
    }

    @Test
    void decodeMessageRequestMinimal() {
        final var data = new MessageRequest(1111, 2222, 3333);
        final var buffer = ByteBuffer.allocate(17);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);
        Assertions.assertNotNull(decoded);
        final var index = ((MessageRequest) decoded).getIndex();
        final var begin = ((MessageRequest) decoded).getBegin();
        final var length = ((MessageRequest) decoded).getLength();

        Assertions.assertEquals(REQUEST, decoded.getMessageType());
        Assertions.assertEquals(1111, index);
        Assertions.assertEquals(2222, begin);
        Assertions.assertEquals(3333, length);
        Assertions.assertEquals(0, buffer.remaining());
    }

    @Test
    void decodeMessageRequestEmpty() {
        final var data = new MessageRequest(1111, 2222, 3333);
        final var buffer = ByteBuffer.allocate(16);
        buffer.put(data.getData(), 0, 16);
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer);

        Assertions.assertTrue(decoded.isEmpty());
        Assertions.assertEquals(16, buffer.remaining());
    }

    @Test
    void decodeMessagePiece() {
        final var array = ByteUtils.getRandomByteArray(10);
        final var data = new MessagePiece(1111, 2222, array);
        final var buffer = ByteBuffer.allocate(4096);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);
        Assertions.assertNotNull(decoded);
        final var index = ((MessagePiece) decoded).getIndex();
        final var begin = ((MessagePiece) decoded).getBegin();
        final var arrayDecoded = ((MessagePiece) decoded).getPiece();

        Assertions.assertEquals(PIECE, decoded.getMessageType());
        Assertions.assertEquals(1111, index);
        Assertions.assertEquals(2222, begin);
        Assertions.assertArrayEquals(array, arrayDecoded);
        Assertions.assertEquals(4096 - 4 - 1 - 4 - 4 - array.length, buffer.remaining());
    }

    @Test
    void decodeMessagePieceMinimal() {
        final var array = ByteUtils.getRandomByteArray(10);
        final var data = new MessagePiece(1111, 2222, array);
        final var buffer = ByteBuffer.allocate(23);
        buffer.put(data.getData());
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer).orElse(null);
        Assertions.assertNotNull(decoded);
        final var index = ((MessagePiece) decoded).getIndex();
        final var begin = ((MessagePiece) decoded).getBegin();
        final var arrayDecoded = ((MessagePiece) decoded).getPiece();

        Assertions.assertEquals(PIECE, decoded.getMessageType());
        Assertions.assertEquals(1111, index);
        Assertions.assertEquals(2222, begin);
        Assertions.assertArrayEquals(array, arrayDecoded);
        Assertions.assertEquals(0, buffer.remaining());
    }

    @Test
    void decodeMessagePieceEmpty() {
        final var array = ByteUtils.getRandomByteArray(10);
        final var data = new MessagePiece(1111, 2222, array);
        final var buffer = ByteBuffer.allocate(22);
        buffer.put(data.getData(), 0, 1228);
        buffer.rewind();

        final var decoded = decoderService.decodeMessage(buffer);

        Assertions.assertTrue(decoded.isEmpty());
        Assertions.assertEquals(22, buffer.remaining());
    }


}