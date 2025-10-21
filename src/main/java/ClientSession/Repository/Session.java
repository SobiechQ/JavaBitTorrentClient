package ClientSession.Repository;

import Peer.Model.Peer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;

@Slf4j
record Session(@NonNull AsynchronousSocketChannel socket, @NonNull Peer peer) implements AutoCloseable {
    @Override
    public void close(){
        try {
            this.socket.close();
        } catch (IOException e) {
            log.warn("Unable to close session {}", e.getCause().getMessage());
        }
    }
}
