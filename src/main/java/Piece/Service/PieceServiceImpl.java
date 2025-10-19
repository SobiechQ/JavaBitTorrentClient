package Piece.Service;

import Model.Message.MessageHave;
import Model.Message.MessageProjection;
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
