package Message.Service;

import Message.Model.MessageHave;
import Message.Model.MessagePiece;
import Message.Model.MessageProjection;
import Message.Model.MessageRequest;
import Peer.Model.PeerInputProjection;

public interface MessageService {
    MessageProjection decode(PeerInputProjection inputProjection);
    MessageHave have(int index);
    MessageRequest request(int index, int begin);
    MessagePiece piece(PeerInputProjection inputProjection);
}
