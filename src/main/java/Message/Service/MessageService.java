package Message.Service;

import Message.Model.*;
import Peer.Model.PeerInputProjection;

public interface MessageService {
    MessageProjection decode(PeerInputProjection inputProjection);
    MessageHave have(int index);
    MessageRequest request(int index, int begin);
    MessagePiece piece(PeerInputProjection inputProjection);
    MessageBitfield bitfield(PeerInputProjection inputProjection);

}
