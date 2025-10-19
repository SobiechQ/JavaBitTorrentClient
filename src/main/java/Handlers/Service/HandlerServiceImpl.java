package Handlers.Service;

import Model.DecodedBencode.Torrent;
import Model.Message.*;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.Optional;

import static Model.Message.DefaultMessage.*;

@Service
@AllArgsConstructor
@Slf4j
public class HandlerServiceImpl implements HandlerService {
    private final DecoderService decoderService;
    private final PeerService peerService;

    @Override
    public void handleMessageInput(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull ByteBuffer buffer, int length) {
        if (length <= 0) {
            return;
        }
        final var projection = decoderService.decodeMessage(buffer);

        projection.ifPresent(messageProjection -> {
                    final var messageType = messageProjection.getMessageType();
                    switch (messageType) {
                        case KEEP_ALIVE -> handleKeepAlive(torrent, peer);
                        case CHOKE -> handleChoke(torrent, peer);
                        case UNCHOKE -> handleUnchoke(torrent, peer);
                        case INTERESTED -> handleInterested(torrent, peer);
                        case NOT_INTERESTED -> handleNotInterested(torrent, peer);
                    }

                    switch (messageProjection) {
                        case MessageHave have -> this.handleHave(torrent, peer, have);
                        case MessageBitfield bitfield -> this.handleBitfield(torrent, peer, bitfield);
                        case MessageRequest request -> this.handleRequest(torrent, peer, request);
                        case MessagePiece piece -> this.handlePiece(torrent, peer, piece);
                        default -> {}
                    }
                    this.handleMessageInput(torrent, peer, buffer, length - messageProjection.getData().length);
                }
        );
    }

    private void handleKeepAlive(@NonNull Torrent torrent, @NonNull Peer peer) {
        log.info("Received KEEP_ALIVE message from peer {}", peer);
    }

    private void handleChoke(@NonNull Torrent torrent, @NonNull Peer peer) {
        log.info("Received CHOKE message from peer {}", peer);
    }

    private void handleUnchoke(@NonNull Torrent torrent, @NonNull Peer peer) {
        log.info("Received UNCHOKE message from peer {}", peer);
    }

    private void handleInterested(@NonNull Torrent torrent, @NonNull Peer peer) {
        log.info("Received INTERESTED message from peer {}", peer);
    }

    private void handleNotInterested(@NonNull Torrent torrent, @NonNull Peer peer) {
        log.info("Received NOT_INTERESTED message from peer {}", peer);
    }

    private void handleHave(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageHave have) {
        log.info("Received HAVE message from peer {} Message {}", peer, have);
        peerService.handleHave(torrent, peer, have);
    }

    private void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        log.info("Received BITFIELD message from peer {} Message {}", peer, bitfield);
        peerService.handleBitfield(torrent, peer, bitfield);
    }

    private void handleRequest(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageRequest request) {
        log.info("Received REQUEST message from peer {} Message {}", peer, request);
    }

    private void handlePiece(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessagePiece piece) {
        log.info("Received PIECE message from peer {} Message {}", peer, piece);
    }

    private void handleCancel(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageProjection cancel) {
        log.info("Received CANCEL message from peer {} Message {}", peer, cancel);
    }

    private void handlePort(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageProjection port) {
        log.info("Received PORT  message from peer {} Message {}", peer, port);
    }
}
