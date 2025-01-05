package org.example;

import Bencode.Bencode;
import Bencode.DecodingError;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) throws DecodingError {
        final var bencode = Bencode.fromFile(new File("C:\\Users\\Sobiech\\Desktop\\Snufkin - Melody of Moominvalley [FitGirl Repack].torrent"));

        bencode
//                .flatMap(b -> b.asDictionary("info"))
                .ifPresent(
                        System.out::println
                );


        bencode
                .flatMap(b -> b.asDictionary("announce"))
                .flatMap(Bencode::asString)
//                .stream().peek(System.out::println)
//                .findFirst()
                .map(string -> {
                    return Try.of(() -> string)
                            .mapTry(s -> new URI("http://bttracker.debian.org:6969/announce").toURL())
                            .mapTry(URL::openConnection)
                            .mapTry(URLConnection::getInputStream)
                            .mapTry(InputStreamReader::new)
                            .mapTry(BufferedReader::new)
                            .toEither()
                            .peekLeft(s -> System.out.println(s))
//                            .peek(s-> System.out.println(s))
                            .toJavaStream()
                            .flatMap(BufferedReader::lines)
                            .collect(Collectors.joining());
                })

                .ifPresent(s -> {

                    System.out.println(s);
                });

    }
}