package Utils;

import TCP.PeerMessage;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class ByteUtils {
    public static int bytesToInt(byte[] bytes) {
        int result = 0;
        for (byte aByte : bytes) {
            result <<= 8;
            result |= (aByte & 0xFF);
        }
        return result;
    }

    public static byte[] intToBytes(int value) {
        return new byte[]{
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }
    public static Stream<Byte> bytesToStream(byte[] bytes){
        return IntStream.range(0, bytes.length)
                .mapToObj(i -> bytes[i]);
    }

    public static byte[] intsToBytes(int[] data) {
        return Arrays.stream(data)
                .boxed()
                .map(ByteUtils::intToBytes)
                .flatMap(ByteUtils::bytesToStream)
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> {
                            byte[] arr = new byte[list.size()];
                            for (int i = 0; i < list.size(); i++) {
                                arr[i] = list.get(i);
                            }
                            return arr;
                        }

                ));
    }
    public static byte[] unbox(List<Byte> bytes){
        return ByteUtils.unbox(bytes.toArray(Byte[]::new));
    }
    public static byte[] unbox(Byte[] bytes){
        final var arr = new byte[bytes.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = bytes[i];
        }
        return arr;
    }

    public static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }
}
