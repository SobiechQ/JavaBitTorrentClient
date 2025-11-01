package Model.DecodedBencode;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;

/**
 * @param path for example: ["data", "images", "cat.png"] => data/images/cat.png
 */
public record FileDataProjection(long length, List<String> path) {

    public Path getDownloadsPath(@NotNull Torrent torrent) {
        final var fileName = String.join("/", this.path());
        final var home = System.getProperty("user.home");
        return Path.of(home, "Downloads", "Downloaded Torrents", torrent.getName(), fileName);
    }
}
