package Log;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class LogTests {

    @Test
    void testLogs() {
        log.warn("warn");
    }
}
