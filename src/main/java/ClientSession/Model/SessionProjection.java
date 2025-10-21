package ClientSession.Model;

import Peer.Model.Peer;
import lombok.NonNull;

import java.nio.channels.AsynchronousSocketChannel;

public record SessionProjection(@NonNull AsynchronousSocketChannel socket, @NonNull Peer peer) {
    //todo czy na pewno projection potrzebnuje socket? lepiej same informacje wyciągnięte z socketa
}
