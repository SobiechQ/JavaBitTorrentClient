package Configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.swing.*;

@EnableAsync
@SpringBootApplication(scanBasePackages = {"Configuration", "Tracker", "ClientSession", "AsyncServer", "Handshake", "Model", "Peer", "Piece", "AsyncClient", "Handlers"})
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Frame::new);
        SpringApplication.run(Main.class, args);
    }
}