package Message.Service;

import Message.Model.*;
import Peer.Model.PeerDataInputProjection;

public interface MessageService {
    int getLength(byte[] lengthPrefix);
    MessageProjection decode(PeerDataInputProjection inputProjection);
    MessageHave have(int index);
    MessageRequest request(int index, int begin);
    MessagePiece piece(PeerDataInputProjection inputProjection);
    MessageBitfield bitfield(PeerDataInputProjection inputProjection);

}
