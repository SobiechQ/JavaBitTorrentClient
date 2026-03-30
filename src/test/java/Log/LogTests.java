package Log;

import Configuration.Main;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = Main.class)
public class LogTests {

    @Test
    void testLogs() {
        log.warn("warn");
    }
}
