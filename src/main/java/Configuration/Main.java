package Configuration;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.swing.*;
import java.io.IOException;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"Configuration", "Tracker", "ClientSession", "AsyncServer", "Handshake", "Model", "Peer", "Piece", "AsyncClient", "Handlers", "Decoder", "File"})
public class Main {
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(LogWindow::new);
        SpringApplication.run(Main.class, args);
        try {
            // Create a terminal
            Terminal terminal = TerminalBuilder.builder().system(true).build();

            // Create a line reader
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).build();

            // Read lines from the user
            while (true) {
                String line = reader.readLine("prompt> ");

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