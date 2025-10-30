package File.Service;

import Model.DecodedBencode.FileDataProjection;
import Model.DecodedBencode.Torrent;
import Piece.Model.PieceProjection;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    @Override
    public void handleCompletedPiece(@NonNull Torrent torrent, @NonNull PieceProjection pieceProjection) {
        log.info("Handling completed piece in file");
        if (!filesExist(torrent))
            createFiles(torrent);
    }

    private boolean filesExist(@NonNull Torrent torrent) {
        if (torrent.isMultifile()) {
            return torrent.getFiles()
                    .map(FileDataProjection::path)
                    .map(l -> String.join("/", l))
                    .map(File::new)
                    .allMatch(File::isFile);
        }
        return new File(torrent.getName()).isFile();
    }

    private void createFiles(@NonNull Torrent torrent) {
        if (torrent.isMultifile()) {
            torrent.getFiles()
                    .map(f -> new Tuple2<>(f.length(), f.path()))
                    .map(t -> t.map2(strings -> Seq
                            .of("Downloaded Torrents")
                            .append(torrent.getName())
                            .append(strings)
                            .toUnmodifiableList()))
                    .map(t -> t.map2(l -> String.join("/", l)))
                    .forEach(t -> this.createFile(t.v2, t.v1));
            return;
        }
        this.createFile(torrent.getName(), torrent.getLength());
    }

    private void createFile(String fileName, long size) {
        final var home = System.getProperty("user.home");
        final var path = Path.of(home, "Downloads", fileName);

        if (path.getParent() != null) {
            try {
                Files.createDirectories(path.getParent());
            } catch (IOException e) {
                log.warn("Unable to create directories {}", e.getMessage());
                e.printStackTrace();
            }
        }
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            fc.position(size - 1);
            fc.write(ByteBuffer.wrap(new byte[]{0}));
        } catch (IOException e) {
            log.warn("Unable to create file {}", e.getMessage());
            e.printStackTrace();
        }
    }
}
