package Utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class ByteUtils {

    public static byte[] getRandomByteArray(int length) {
        return ByteUtils.unbox(Stream.generate(() -> Math.random() * 255)
                .map(i -> (byte) Math.round(i))
                .limit(length)
                .toList());
    }

    public static boolean[] byteToBits(byte b) {
        boolean[] bits = new boolean[8];
        for (int i = 0; i < 8; i++) {
            bits[7 - i] = (b & (1 << i)) != 0;
        }
        return bits;
    }

    public static byte bitsToByte(boolean[] bits) {
        if (bits.length != 8) {
            throw new IllegalArgumentException("Array length must be 8");
        }
        byte b = 0;
        for (int i = 0; i < 8; i++) {
            if (bits[7 - i]) {
                b |= (1 << i);
            }
        }
        return b;
    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes.length != 4) {
            throw new IllegalArgumentException("Expected 4 bytes, got " + bytes.length);
        }

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

    public static Stream<Boolean> bitsToStream(boolean[] booleans) {
        return IntStream.range(0, booleans.length)
                .mapToObj(i -> booleans[i]);
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
