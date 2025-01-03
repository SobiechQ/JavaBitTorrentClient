package Bencode;

import lombok.EqualsAndHashCode;
@EqualsAndHashCode
public abstract sealed class BencodeValue permits BDictionary, BInt, BList, BString {

}
