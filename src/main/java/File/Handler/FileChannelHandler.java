package File.Handler;

import Model.DecodedBencode.FileDataProjection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;

@Slf4j
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class FileChannelHandler implements CompletionHandler<Integer, Object> {
    private final AsynchronousFileChannel asynchronousFileChannel;
    private final FileDataProjection fileDataProjection;

    @Override
    public void completed(Integer result, Object attachment) {
        log.info("File writing completed for file {}", fileDataProjection);

//        try {
//            this.asynchronousFileChannel.close();
//        } catch (IOException e) {
//            log.warn("Unable to close channel for file {}", fileDataProjection, e);
//        }
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
        log.warn("File writing failed for file {}", fileDataProjection, exc);
//        try {
//            this.asynchronousFileChannel.close();
//        } catch (IOException e) {
//            log.warn("Unable to close channel for file {}", fileDataProjection, e);
//        }
    }
}
