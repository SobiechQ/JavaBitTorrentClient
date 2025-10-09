package Model.DecodedBencode;

import Model.Bencode.Bencode;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
@Getter
public abstract class DecodedBencode {
    private final Bencode bencode;

    public DecodedBencode(Bencode bencode) {
        this.bencode = bencode;
    }

    public DecodedBencode(String encoded) {
        this.bencode = new Bencode(encoded);
    }

    @Override
    public String toString() {
        return bencode.toString();
    }
}
