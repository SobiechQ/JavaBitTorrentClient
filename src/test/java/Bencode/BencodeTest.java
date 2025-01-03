package Bencode;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Stream;

class BencodeTest {

    @Test
    void bencodeDecodeComplex_whenGivenEncoded_decodesCorrectly() {
        final var bencode = new Bencode("d8:contactsld5:email17:nolan@example.com5:phone10:1234567890ed5:email19:info@filmstudio.com5:phone10:0987654321ee4:userd3:agei52e6:moviesld8:releasedi2010e5:title9:Inceptioned8:releasedi2014e5:title12:Interstellared8:releasedi2017e5:title7:Dunkirkee4:name17:Christopher Nolanee");
        /*
        {
            "user":{
            "name":"Christopher Nolan",
                    "age":52,
                    "movies": [
            {
                "title":"Inception", "released":2010
            },
            {
                "title":"Interstellar", "released":2014
            },
            {
                "title":"Dunkirk", "released":2017
            }
            ]
        },
            "contacts": [
            {
                "email":"nolan@example.com", "phone":"1234567890"
            },
            {
                "email":"info@filmstudio.com", "phone":"0987654321"
            }
        ]
        }
         */

        final var age = bencode.asDictionary()
                .flatMap(map -> Optional.ofNullable(map.get("user")))
                .flatMap(b -> b.asDictionary())
                .flatMap(map -> Optional.ofNullable(map.get("age")))
                .flatMap(b -> b.asInteger())
                .orElseThrow(() -> new DecodingError(""));

        Assertions.assertEquals(52, age);

        final var inceptionMap = bencode.asDictionary()
                .flatMap(map -> Optional.ofNullable(map.get("user")))
                .flatMap(b -> b.asDictionary())
                .flatMap(map -> Optional.ofNullable(map.get("movies")))
                .flatMap(b -> b.asList())
                .map(Collection::stream)
                .stream()
                .flatMap(l -> l)
                .flatMap(l -> l.asDictionary().stream())
                .filter(map ->
                        Optional.ofNullable(map.get("title"))
                                .flatMap(b -> b.asString())
                                .map(s -> s.equals("Inception"))
                                .orElse(false)
                )
                .findAny()
                .orElseThrow(() -> new DecodingError(""));

        final var inceptionReleaseDate =
                Optional.ofNullable(inceptionMap.get("released"))
                        .flatMap(b -> b.asInteger())
                        .orElseThrow(() -> new DecodingError(""));


        Assertions.assertEquals(2010, inceptionReleaseDate);

    }

}