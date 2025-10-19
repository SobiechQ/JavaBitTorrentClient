package Piece.Service;

import Model.Message.MessageHave;
import Model.Message.MessageProjection;
import Model.DecodedBencode.Torrent;

import java.util.stream.Stream;

public interface PieceService {
    Stream<MessageProjection> getRequest(Torrent torrent);
    void handleHave(Torrent torrent, MessageHave messageHave);
}
