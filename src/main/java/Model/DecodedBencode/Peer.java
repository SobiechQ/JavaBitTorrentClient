package Model.DecodedBencode;

import Model.Bencode.DecodingError;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public record Peer(InetAddress address, int port) {

    public Peer(List<Integer> src){
        this(getInetAddressByBytes(src), getPortByBytes(src));
    }

    private static InetAddress getInetAddressByBytes(List<Integer> src) {
        final var address = new byte[4];
        for (int i = 0; i < 4; i++)
            address[i] = (byte) src.get(i).intValue();
        try {
            return InetAddress.getByAddress(address);
        } catch (UnknownHostException e) {
            throw new DecodingError(e);
        }
    }

    private static int getPortByBytes(List<Integer> src) {
        return src.get(4) * 256 + src.get(5);
    }
}
