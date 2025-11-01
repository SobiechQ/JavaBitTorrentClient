package File.Handler;

import Model.DecodedBencode.FileDataProjection;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.channels.AsynchronousFileChannel;

@AllArgsConstructor
@Component
public class FileChannelHandlerFactory {
    public FileChannelHandler getFileChannelHandler(@NonNull AsynchronousFileChannel asynchronousFileChannel, @NonNull FileDataProjection fileDataProjection) {
        return new FileChannelHandler(asynchronousFileChannel, fileDataProjection);
    }
}
