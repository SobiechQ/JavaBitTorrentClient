package Message.Service;

import Message.Model.PeerMessageHave;
import Message.Model.PeerMessagePiece;
import Message.Model.PeerMessageProjection;
import Message.Model.PeerMessageRequest;
import Peer.Model.PeerInputProjection;

public interface MessageService {
    PeerMessageProjection decode(PeerInputProjection inputProjection);
    PeerMessageHave have(int index);
    PeerMessageRequest request(int index, int begin);
    PeerMessagePiece piece(PeerInputProjection inputProjection);
}
