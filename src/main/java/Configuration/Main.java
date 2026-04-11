package Configuration;

import CLI.Service.CliService;
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
@SpringBootApplication(scanBasePackages = {"Configuration", "Tracker", "ClientSession", "AsyncServer", "Handshake", "Model", "Peer", "Piece", "AsyncClient", "CLI", "Decoder", "File"})

public class Main {
    public static void main(String[] args) throws IOException {
        SwingUtilities.invokeLater(LogWindow::new);
        SpringApplication.run(Main.class, args);
    }

}