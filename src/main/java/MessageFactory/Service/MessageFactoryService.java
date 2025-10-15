package MessageFactory.Service;

import MessageFactory.Model.*;
import Peer.Model.PeerDataInputProjection;

public interface MessageFactoryService {
    int getLength(byte[] lengthPrefix);
    MessageProjection decode(PeerDataInputProjection inputProjection);
    MessageHave have(int index);
    MessageRequest request(int index, int begin);
    MessagePiece piece(PeerDataInputProjection inputProjection);
    MessageBitfield bitfield(PeerDataInputProjection inputProjection);

}
