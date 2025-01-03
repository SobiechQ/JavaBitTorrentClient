package Bencode;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.jooq.lambda.tuple.Tuple2;
@EqualsAndHashCode
public abstract sealed class BencodeValue permits BDictionary, BInt, BList, BString {



//    public abstract Tuple2<? extends BencodeValue, String> decode(@NonNull String encoded);

}
