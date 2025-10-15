package AsyncClient;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;

@Controller
public class AsyncServerTest {
    private final AsynchronousServerSocketChannel listener;

    public AsyncServerTest(ExecutorService executor) throws IOException {
        this.listener = AsynchronousServerSocketChannel.open();
    }

    @PostConstruct
    public void postConstruct() {
        try {
            this.listener.bind(new InetSocketAddress("127.0.0.1", 8081));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.accept();
    }

    private void accept() {
        listener.accept(ByteBuffer.allocate(1024), new CompletionHandler<AsynchronousSocketChannel, ByteBuffer>() {
            @Override
            public void completed(AsynchronousSocketChannel client, ByteBuffer attachment) {
                client.read(attachment, attachment, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        buffer.flip();
                        String msg = new String(buffer.array(), 0, buffer.limit()).trim();
                        String response = "Processed: " + msg;
                        client.write(ByteBuffer.wrap(response.getBytes()));
                        buffer.clear();
                        client.read(buffer, buffer, this);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            client.close();
                        } catch (IOException ignored) {
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {

            }
        });
    }
}
