package Model.Bencode;

public class DecodingError extends Error {
    public DecodingError(String message) {
        super(message);
    }

    public DecodingError(Throwable cause) {
        super(cause);
    }
}
