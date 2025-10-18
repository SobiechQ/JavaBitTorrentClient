package AsyncClient;

import ClientSession.Controller.ClientSessionController;
import Model.DecodedBencode.Torrent;
import Peer.Controller.PeerController;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;

@Controller
@AllArgsConstructor
public class AsyncServerTest {
    private final ClientSessionController controller;
    private final PeerController peerController;
    private final static Torrent MOCK_TORRENT;

    static {
        try {
            MOCK_TORRENT = Torrent.fromFile(new File("src/test/java/resources/ubuntu-25.04-desktop-amd64.iso.torrent"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void postConstruct() {

    }
}
