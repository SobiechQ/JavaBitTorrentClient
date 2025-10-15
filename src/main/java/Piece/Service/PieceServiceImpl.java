package Piece.Service;

import MessageFactory.Model.MessageHave;
import MessageFactory.Model.MessageProjection;
import Model.DecodedBencode.Torrent;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class PieceServiceImpl implements PieceService {
    @Override
    public Stream<MessageProjection> getRequest(@NonNull Torrent torrent) {
        return Stream.of();
    }

    @Override
    public void handleHave(Torrent torrent, MessageHave messageHave) {

    }
}
