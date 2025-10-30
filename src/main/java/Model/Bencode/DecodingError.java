package Model.Bencode;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DecodingError extends Error {
    @Nullable
    private final String received;

    public DecodingError(Throwable throwable){
        super(throwable);
        this.received = null;
    }

    public DecodingError(String message) {
        this(message, null);
    }

    public DecodingError(String message, @Nullable String received) {
        super(message);
        this.received = received;
    }


    @Override
    public String getMessage() {
        return this.getReceived()
                .map(r -> String.format("%sReceived: %s", super.getMessage(), r))
                .orElse(super.getMessage());
    }

    private Optional<String> getReceived() {
        return Optional.ofNullable(this.received);
    }
}
