package Piece.Service;

import Model.Bencode.Bencode;
import Model.Message.MessagePiece;
import Model.DecodedBencode.Torrent;
import Model.Message.MessageRequest;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import Peer.Service.PeerStrategyService;
import Piece.Repository.PieceRepository;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Seq;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

import static Model.Message.MessageRequest.REQUEST_LENGTH;

@Service
@Slf4j
@AllArgsConstructor
public class PieceServiceImpl implements PieceService {
    private final PieceRepository pieceRepository;
    private final PeerStrategyService peerStrategyService;
    private final PeerService peerService;

    @Override
    public Optional<MessageRequest> getRequest(@NonNull Torrent torrent, @NonNull Peer peer) {
        return Seq.ofType(peerStrategyService.getPiecesRarest(torrent), Integer.class)
                .filter(i -> !pieceRepository.isPieceComplete(torrent, i))
                .filter(i -> peerService.isPieceAvailable(torrent, peer, i))
                .limit(20)
                .shuffle()
                .findFirst()
                .map(i -> this.toRequest(torrent, i));
    }

    @Override
    public Optional<MessageRequest> handlePiece(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessagePiece messagePiece) {
        final var index = messagePiece.getIndex();
        final var begin = messagePiece.getBegin();
        final var payload = messagePiece.getPiece();

        pieceRepository.handlePiece(torrent, index, begin, payload);
        if (pieceRepository.isPieceComplete(torrent, index)) {
            final var piece = pieceRepository.getCompletedPiece(torrent, index);
            if (this.verifyHash(torrent, index, piece)) {
                log.info("Piece part verified successfully");
            } else {
                log.warn("Piece verification failed");
            }
            return Optional
                    .of(this.getRequest(torrent, peer))
                    .flatMap(r -> r);
        }

        return Optional.of(this.toRequest(torrent, index));
    }

    private int getNextLength(@NonNull Torrent torrent, int index) {
        final var remaining = this.pieceRepository
                .getPieceProjection(torrent, index)
                .remaining();
        return Math.clamp(remaining, 0, REQUEST_LENGTH);
    }

    private MessageRequest toRequest(@NonNull Torrent torrent, int index) {
        final var begin = pieceRepository.getNextBegin(torrent, index);
        final var length = this.getNextLength(torrent, index);
        return new MessageRequest(index, begin, length);
    }

    private boolean verifyHash(@NonNull Torrent torrent, int index, byte[] piece) {
        final var checksum = torrent.getPieceHash(index);
        final var calculated = Hashing.sha1().hashBytes(piece).asBytes();
        return checksum.map(h -> Arrays.equals(h, calculated)).orElse(false);
    }
}
