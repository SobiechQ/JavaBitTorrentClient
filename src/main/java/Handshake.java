import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * <a href="https://www.bittorrent.org/beps/bep_0003.html#peer-protocol">Bep 003 impl</a>
 */
public class Handshake {
    public static byte[] apply(byte[] infoHash, byte[] peerId){
        if (infoHash.length != 20 || peerId.length != 20)
            throw new IllegalArgumentException("Passed arrays have to be 20 bytes long.");

        final var handshake = new byte[68];
        handshake[0] = 19;

        final var protocolName = "BitTorrent protocol";
        for (int i = 0; i < protocolName.getBytes().length; i++)
            handshake[i + 1] = (byte) protocolName.charAt(i);

        System.arraycopy(infoHash, 0, handshake, 28, infoHash.length);
        System.arraycopy(peerId, 0, handshake, 48, infoHash.length);

        return handshake;
    }
}
