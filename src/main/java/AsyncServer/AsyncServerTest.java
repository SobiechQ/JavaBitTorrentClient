package AsyncServer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@Controller
@Slf4j
public class AsyncServerTest {
    private final AsynchronousServerSocketChannel listener;

    public AsyncServerTest() throws IOException {
        this.listener = AsynchronousServerSocketChannel.open();
    }

//    @Async
//    @PostConstruct
//    public void postConstruct() {
//        try {
////            final var inet = new InetSocketAddress("127.0.0.1", 6881); //set to random and inform tracker about port in use
//            this.listener.bind(null);
//            log.info("Server listening on address {}", this.listener.);
//            this.accept();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    private void accept() {
        listener.accept(ByteBuffer.allocate(1024), new CompletionHandler<AsynchronousSocketChannel, ByteBuffer>() {
            @Override
            public void completed(AsynchronousSocketChannel client, ByteBuffer buffer) {
                log.info("Accepted input");
                CompletableFuture<ByteBuffer> completableFuture = new CompletableFuture<>();
                client.read(buffer, null, new CompletionHandler<>() {

                    @Override
                    public void completed(Integer result, Object attachment) {
                        completableFuture.complete(buffer);
                    }

                    @Override
                    public void failed(Throwable exc, Object attachment) {
                        completableFuture.completeExceptionally(exc);
                    }
                });
                completableFuture.whenComplete((buffer1, throwable) -> {
                    System.out.println(buffer1.toString());

                });

                accept();
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });

    }
}
