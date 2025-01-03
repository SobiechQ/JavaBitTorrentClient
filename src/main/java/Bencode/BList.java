package Bencode;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.jooq.lambda.Seq;
import org.jooq.lambda.tuple.Tuple2;
import java.util.LinkedList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = false)
public final class BList extends BencodeValue {
    final List<Bencode> listValue;

    public BList(@NonNull String encoded) {
        this.listValue = BList.decode(encoded).v1.getListValue();
    }
    public BList(@NonNull List<Bencode> listValue){
        this.listValue = listValue;
    }

    public static Tuple2<BList, String> decode(@NonNull final String encoded) throws DecodingError {
        if (encoded.length() < 2 || encoded.charAt(0) != 'l') //If it's not a list, return empty
            throw new DecodingError("Encoded value does not represent list");

        final var remaining = Seq.ofType(encoded.chars().boxed(), Integer.class)
                .skip(1)
                .map(c -> (char) c.intValue())
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        final List<Bencode> decoded = new LinkedList<>();
        while (!remaining.isEmpty() && remaining.charAt(0) != 'e'){
            final var decodedAndRemaining = Bencode.decode(remaining.toString());
            decoded.add(decodedAndRemaining.v1);
            remaining.setLength(0);
            remaining.append(decodedAndRemaining.v2);

        }
        remaining.deleteCharAt(0);

        return new Tuple2<>(decoded, remaining.toString())
                .map1(l->new BList(l));
    }
}
