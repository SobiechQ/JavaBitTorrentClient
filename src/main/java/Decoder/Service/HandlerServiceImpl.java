package Decoder.Service;

import Model.DecodedBencode.Torrent;
import Model.Message.*;
import Peer.Model.Peer;
import Peer.Service.PeerService;
import Piece.Service.PieceService;
import Utils.ByteUtils;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class HandlerServiceImpl implements HandlerService {
    private final DecoderService decoderService;
    private final PeerService peerService;
    private final PieceService pieceService;

    @Override
    public List<MessageProjection> handleMessageInput(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull ByteBuffer buffer, int length) {
        return this.handleMessageInput(torrent, peer, buffer, length, new LinkedList<>());
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

    private List<MessageProjection> handleMessageInput(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull ByteBuffer buffer, int length, @NonNull List<MessageProjection> list) {
        if (length <= 0) {
            return list;
        }
        final var projection = decoderService.decodeMessage(buffer);

        projection.flatMap(messageProjection -> switch (messageProjection) {
                    case MessageKeepAlive _ -> handleKeepAlive(torrent, peer);
                    case MessageChoke _ -> handleChoke(torrent, peer);
                    case MessageUnchoke _ -> handleUnchoke(torrent, peer);
                    case MessageInterested _ -> handleInterested(torrent, peer);
                    case MessageNotInterested _ -> handleNotInterested(torrent, peer);
                    case MessageHave have -> this.handleHave(torrent, peer, have);
                    case MessageBitfield bitfield -> this.handleBitfield(torrent, peer, bitfield);
                    case MessageRequest request -> this.handleRequest(torrent, peer, request);
                    case MessagePiece piece -> this.handlePiece(torrent, peer, piece);
                    case MessageCancel cancel -> this.handleCancel(torrent, peer, cancel);
                    case MessagePort port -> this.handlePort(torrent, peer, port);
                    default -> throw new IllegalStateException();
                })
                .ifPresent(p -> {
                    list.add(p);
                    this.handleMessageInput(torrent, peer, buffer, length - p.getData().length, list);
                });
        return list;
    }


    private Optional<MessageProjection> handleKeepAlive(@NonNull Torrent torrent, @NonNull Peer peer) {
        //log.info("Received KEEP_ALIVE message from peer {}", peer);
        return Optional.empty();
    }

    private Optional<MessageProjection> handleChoke(@NonNull Torrent torrent, @NonNull Peer peer) {
        //log.info("Received CHOKE message from peer {}", peer);
        return Optional.empty();
    }

    private Optional<MessageProjection> handleUnchoke(@NonNull Torrent torrent, @NonNull Peer peer) {
        //log.info("Received UNCHOKE message from peer {}", peer);
        return pieceService
                .getRequest(torrent, peer)
                .map(r -> r);
    }

    private Optional<MessageProjection> handleInterested(@NonNull Torrent torrent, @NonNull Peer peer) {
        //log.info("Received INTERESTED message from peer {}", peer);
        return Optional.empty();
    }

    private Optional<MessageProjection> handleNotInterested(@NonNull Torrent torrent, @NonNull Peer peer) {
        //log.info("Received NOT_INTERESTED message from peer {}", peer);
        return Optional.empty();
    }

    private Optional<MessageProjection> handleHave(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageHave have) {
        //log.info("Received HAVE message from peer {} Message {}", peer, have);
        peerService.handleHave(torrent, peer, have);
        return Optional.empty();
    }

    private Optional<MessageProjection> handleBitfield(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageBitfield bitfield) {
        //log.info("Received BITFIELD message from peer {} Message {}", peer, bitfield);
        peerService.handleBitfield(torrent, peer, bitfield);
        return pieceService
                .getRequest(torrent, peer)
                .map(r -> r);
    }

    private Optional<MessageProjection> handleRequest(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageRequest request) {
        //log.info("Received REQUEST message from peer {} Message {}", peer, request);
        return Optional.empty();
    }

    private Optional<MessageProjection> handlePiece(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessagePiece piece) {
        //log.info("Received PIECE message from peer {} Message {}", peer, piece);

        return pieceService.handlePiece(torrent, peer, piece)
                .map(p -> p);
    }

    private Optional<MessageProjection> handleCancel(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageProjection cancel) {
        //log.info("Received CANCEL message from peer {} Message {}", peer, cancel);
        return Optional.empty();
    }

    private Optional<MessageProjection> handlePort(@NonNull Torrent torrent, @NonNull Peer peer, @NonNull MessageProjection port) {
        //log.info("Received PORT  message from peer {} Message {}", peer, port);
        return Optional.empty();
    }
}
