package Configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"Configuration", "Tracker", "AsyncClient", "AsyncServer", "Handshake", "MessageFactory", "Model", "Peer", "Piece"})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}