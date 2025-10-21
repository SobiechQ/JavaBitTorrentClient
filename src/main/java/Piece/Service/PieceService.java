package Piece.Service;

import Model.Message.MessageHave;
import Model.Message.MessagePiece;
import Model.Message.MessageProjection;
import Model.DecodedBencode.Torrent;
import Model.Message.MessageRequest;
import Peer.Model.Peer;

import java.util.Optional;
import java.util.stream.Stream;

public interface PieceService {
    Optional<MessageRequest> getRequest(Torrent torrent, Peer peer);
    Optional<MessageRequest> handlePiece(Torrent torrent, Peer peer, MessagePiece messagePiece);
}
