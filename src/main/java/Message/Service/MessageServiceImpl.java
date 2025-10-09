package Message.Service;

import Message.Model.*;
import Peer.Model.PeerInputProjection;
import org.springframework.stereotype.Service;
import static Message.Model.DefaultPeerMessage.*;
import static Utils.ByteUtils.bytesToInt;

@Service
public class MessageServiceImpl implements MessageService {
    private final static int REQUEST_LENGTH = (int) Math.pow(2, 14);

    @Override
    public PeerMessageProjection decode(PeerInputProjection strategyInput) {
        final var data = strategyInput.data();

        if (data.length == 0)
            return KEEP_ALIVE.getProjection();

        final var receivedMessageType = data[0];
        final var receivedPayload = new byte[data.length - 1];
        System.arraycopy(data, 1, receivedPayload, 0, receivedPayload.length);

        return new PeerMessageProjection(MessageType.valueOf(receivedMessageType), receivedPayload);
    }

    @Override
    public PeerMessageHave have(int index) {
        return new PeerMessageHave(index);
    }

    @Override
    public PeerMessageRequest request(int index, int begin) {
        return new PeerMessageRequest(index, begin, REQUEST_LENGTH);
    }

    @Override
    public PeerMessagePiece piece(PeerInputProjection inputProjection) {
        final var data = inputProjection.data();

        final var receivedIndex = new byte[4];
        System.arraycopy(data, 1, receivedIndex, 0, 4);

        final var receivedBegin = new byte[4];
        System.arraycopy(data, 5, receivedBegin, 0, 4);

        final var receivedPiece = new byte[data.length - 9];
        System.arraycopy(data, 9, receivedPiece, 0, data.length - 9);
        
        return new PeerMessagePiece(bytesToInt(receivedIndex), bytesToInt(receivedBegin), receivedPiece);
    }


}
