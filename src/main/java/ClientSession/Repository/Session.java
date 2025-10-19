package ClientSession.Repository;

import Peer.Model.Peer;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

public record Session(@NonNull AsynchronousSocketChannel socket, @NonNull Peer peer) implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(Session.class);

    @Override
    public void close(){
        try {
            this.socket.close();
        } catch (IOException e) {
            log.warn("Unable to close session {}", e.getCause().getMessage());
        }
    }
}
