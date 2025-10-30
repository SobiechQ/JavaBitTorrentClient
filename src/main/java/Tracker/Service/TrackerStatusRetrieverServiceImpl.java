package Tracker.Service;

import Model.DecodedBencode.Torrent;
import Piece.Model.PieceStatusProjection;
import Piece.Repository.PieceRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TrackerStatusRetrieverServiceImpl implements TrackerStatusRetrieverService {
    private final PieceRepository pieceRepository;

    @Override
    public long getUploaded(@NonNull Torrent torrent) {
        return 0;
    }

    @Override
    public long getDownloaded(@NonNull Torrent torrent) {
        return pieceRepository.getPieceStatusProjection(torrent)
                .map(PieceStatusProjection::downloaded)
                .mapToInt(i -> i)
                .sum();
    }

    @Override
    public long getLeft(@NonNull Torrent torrent) {
        final var downloaded = this.getDownloaded(torrent);
        return torrent.getLength() - downloaded;
    }
}
