package CLI.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
class CliServiceImpl implements CliService {

    @PostConstruct
    public void run() {
        try {
            Terminal terminal = TerminalBuilder.builder().system(true).build();

            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            while (true) {
                String line = reader.readLine("JBTC> ");

                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }

                terminal.writer().println("You entered: " + line);

                terminal.flush();
            }

            terminal.writer().println("Goodbye!");
            terminal.close();

        } catch (IOException e) {
            System.err.println("Error creating terminal: " + e.getMessage());
        }
    }
}
