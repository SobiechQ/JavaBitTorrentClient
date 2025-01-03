package Bencode;

import lombok.EqualsAndHashCode;
@EqualsAndHashCode
abstract sealed class BencodeValue permits BDictionary, BInt, BList, BString {
    public abstract String encode();
}
