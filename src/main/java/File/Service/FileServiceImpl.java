package File.Service;

import File.Handler.FileChannelHandler;
import File.Handler.FileChannelHandlerFactory;
import Model.DecodedBencode.FileDataProjection;
import Model.DecodedBencode.Torrent;
import Piece.Model.PieceProjection;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileChannelHandlerFactory fileChannelHandlerFactory;
    private final Map<Path, AsynchronousFileChannel> channels = new ConcurrentHashMap<>();

    @Override
    public void handleCompletedPiece(@NonNull Torrent torrent, @NonNull PieceProjection pieceProjection) {
        if (!filesExist(torrent))
            createFiles(torrent);
        try {
            this.writeData(torrent, pieceProjection);
        } catch (IOException e) {
            log.warn("Unable to write data to file", e);
        }
    }

    private void writeData(@NonNull Torrent torrent, @NonNull PieceProjection pieceProjection) throws IOException {

        if (torrent.isMultifile()) {
            final var files = torrent.getFiles().collect(Collectors.toUnmodifiableSet());
            var fileBegin = pieceProjection.getAbsoluteBegin();
            var length = pieceProjection.getPiece().length;
            for (FileDataProjection file : files) {
                if (length <= 0)
                    break;
                if (fileBegin - file.length() >= 0) {
                    fileBegin -= (int) file.length();
                    continue;
                }
                final var pieceBegin = pieceProjection.getPiece().length - length;
                this.writeFile(torrent, fileBegin, pieceBegin, file, pieceProjection);
                length -= (int) (file.length() - fileBegin);
                fileBegin = Math.clamp(fileBegin - (int) file.length(), 0, Integer.MAX_VALUE);
            }
            return;
        }
        //todo not multifile
    }

    private void writeFile(@NonNull Torrent torrent, long fileBegin, int pieceBegin, @NonNull FileDataProjection file, @NonNull PieceProjection piece) throws IOException {
        final var path = file.getDownloadsPath(torrent);
        final var channel = getChannel(path);
        final var length = Math.toIntExact(Math.min(piece.getPiece().length - pieceBegin, file.length() - fileBegin));
        final var buffer = ByteBuffer.allocate(length);

        final var selectedPiece = new byte[length];
        System.arraycopy(piece.getPiece(), pieceBegin, selectedPiece, 0, length);
        buffer.put(0, selectedPiece);
        buffer.rewind();

        log.info("Writing to file: {}, on Path: {} buffer: {}, on position: {}, piece begin: {}, length {}", file, path, buffer, fileBegin, pieceBegin, length);
        channel.write(buffer, fileBegin, null, fileChannelHandlerFactory.getFileChannelHandler(channel, file));
    }

    private boolean filesExist(@NonNull Torrent torrent) {
        if (torrent.isMultifile()) {
            return torrent.getFiles()
                    .map(f -> f.getDownloadsPath(torrent))
                    .map(Path::toFile)
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
                log.warn("Unable to create directories", e);
            }
        }
        try (FileChannel fc = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            log.info("Created new file");
            fc.position(size - 1);
            fc.write(ByteBuffer.wrap(new byte[]{0}));
        } catch (IOException e) {
            log.warn("Unable to create file", e);
        }
    }

    private AsynchronousFileChannel getChannel(@NonNull Path path) {
        return this.channels.computeIfAbsent(path, _ -> {
            try {
                return AsynchronousFileChannel.open(path, StandardOpenOption.WRITE);
            } catch (IOException e) {
                log.warn("Unable to open file channel", e);
                throw new RuntimeException(e);
            }
        });
    }
}
