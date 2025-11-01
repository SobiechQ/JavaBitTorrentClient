package Decoder.Service;

import Decoder.Event.MessageBitfieldEvent;
import Decoder.Event.MessageHaveEvent;
import Decoder.Event.MessagePieceEvent;
import Model.DecodedBencode.Torrent;
import Model.Message.*;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import Piece.Service.PieceService;
import Utils.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Slf4j
public class HandlerServiceImpl implements HandlerService {
    private final DecoderService decoderService;
    private final PeerService peerService;
    private final PieceService pieceService;
    private final ApplicationEventPublisher publisher;

    @Override
    public void handleMessageInput(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull ByteBuffer buffer, int length, @NonNull Consumer<MessageProjection> responseSender) {
        if (length <= 0)
            return;
        final var projection = decoderService.decodeMessage(buffer);

        projection.ifPresent(messageProjection -> {
            switch (messageProjection) {
                case MessageKeepAlive _ -> handleKeepAlive(torrent, peer, responseSender);
                case MessageChoke _ -> handleChoke(torrent, peer, responseSender);
                case MessageUnchoke _ -> handleUnchoke(torrent, peer, responseSender);
                case MessageInterested _ -> handleInterested(torrent, peer, responseSender);
                case MessageNotInterested _ -> handleNotInterested(torrent, peer, responseSender);
                case MessageHave have -> this.handleHave(torrent, peer, have, responseSender);
                case MessageBitfield bitfield -> this.handleBitfield(torrent, peer, bitfield, responseSender);
                case MessageRequest request -> this.handleRequest(torrent, peer, request, responseSender);
                case MessagePiece piece -> this.handlePiece(torrent, peer, piece, responseSender);
                case MessageCancel cancel -> this.handleCancel(torrent, peer, cancel, responseSender);
                case MessagePort port -> this.handlePort(torrent, peer, port, responseSender);
                default -> throw new IllegalStateException();
            }
            this.handleMessageInput(torrent, peer, buffer, length - messageProjection.getData().length, responseSender);
        });
    }

    @Override
    public boolean isMessageComplete(@NonNull ByteBuffer buffer, int length) {
        if (length == 0) {
            return true;
        }
        if (length < 4) {
            return false;
        }
        final var mark = buffer.position();
        buffer.position(0);

        final var lengthPrefixArray = new byte[4];
        buffer.get(lengthPrefixArray);
        final var lengthPrefix = ByteUtils.bytesToInt(lengthPrefixArray);

        buffer.position(mark);
        return mark >= lengthPrefix + 4;
    }

    private void handleKeepAlive(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received KEEP_ALIVE message from peer {}", peer);
    }

    private void handleChoke(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received CHOKE message from peer {}", peer);
    }

    private void handleUnchoke(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received UNCHOKE message from peer {}", peer);
        pieceService
                .getRequest(torrent, peer)
                .ifPresent(responseSender);
    }

    private void handleInterested(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received INTERESTED message from peer {}", peer);
    }

    private void handleNotInterested(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received NOT_INTERESTED message from peer {}", peer);
    }

    private void handleHave(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageHave have, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received HAVE message from peer {} Message {}", peer, have);
        publisher.publishEvent(new MessageHaveEvent(this, torrent, peer, have));
    }

    private void handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received BITFIELD message from peer {} Message {}", peer, bitfield);
        publisher.publishEvent(new MessageBitfieldEvent(this, torrent, peer, bitfield));
        pieceService
                .getRequest(torrent, peer)
                .ifPresent(responseSender);
    }

    private void handleRequest(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageRequest request, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received REQUEST message from peer {} Message {}", peer, request);
    }

    private void handlePiece(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessagePiece piece, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received PIECE message from peer {} Message {}", peer, piece);
        publisher.publishEvent(new MessagePieceEvent(this, torrent, peer, piece, responseSender));
    }

    private void handleCancel(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageProjection cancel, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received CANCEL message from peer {} Message {}", peer, cancel);
    }

    private void handlePort(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageProjection port, @NonNull Consumer<MessageProjection> responseSender) {
        log.info("Received PORT  message from peer {} Message {}", peer, port);
    }
}
