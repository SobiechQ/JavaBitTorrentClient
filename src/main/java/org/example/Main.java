package org.example;

import Bencode.Bencode;
import Bencode.DecodingError;
import java.io.File;

public class Main {
    public static void main(String[] args) throws DecodingError {
        Bencode.fromFile(new File("C:\\Users\\Sobiech\\Desktop\\Snufkin - Melody of Moominvalley [FitGirl Repack].torrent"))
                .ifPresent(System.out::println);

    }
}