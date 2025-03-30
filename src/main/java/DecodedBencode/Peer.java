package DecodedBencode;

import java.net.InetAddress;

public record Peer(InetAddress address, int port) { }
