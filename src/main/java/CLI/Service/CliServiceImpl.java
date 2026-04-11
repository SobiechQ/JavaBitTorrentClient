package CLI.Service;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CliServiceImpl implements CliService {
    public void run() {
        try {
            // Create a terminal
            Terminal terminal = TerminalBuilder.builder().system(true).build();

            // Create a line reader
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            // Read lines from the user
            while (true) {
                String line = reader.readLine("JBTC> ");

                // Exit if requested
                if ("exit".equalsIgnoreCase(line)) {
                    break;
                }

                // Echo the line back to the user
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
