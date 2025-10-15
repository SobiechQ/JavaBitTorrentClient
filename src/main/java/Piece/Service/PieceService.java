package Piece.Service;

import MessageFactory.Model.MessageHave;
import MessageFactory.Model.MessageProjection;
import Model.DecodedBencode.Torrent;

import java.util.stream.Stream;

public interface PieceService {
    Stream<MessageProjection> getRequest(Torrent torrent);
    void handleHave(Torrent torrent, MessageHave messageHave);
}
