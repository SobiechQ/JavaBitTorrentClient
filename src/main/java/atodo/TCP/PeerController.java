package atodo.TCP;

import Model.Message.MessageType;
import Model.DecodedBencode.Torrent;
import Peer.Model.Peer;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class PeerController {
    private final static Logger logger = LogManager.getLogger();
    private final Torrent torrent;
    private final SortedSet<Piece> pieces;


    public PeerController(Torrent torrent) {
        this.torrent = torrent;
        pieces = new TreeSet<>(Comparator.comparingInt(Piece::getId));
    }

    public void open(@NonNull Peer peer) throws IOException {
        logger.info("Opening connection to... {}", peer);
        try (final var socket = new Socket(peer.address(), peer.port())) {
            logger.info("Connection established {}", peer);

            final var msg = PeerMessage.get(socket.getInputStream());

            if (msg.getMessageType() != MessageType.BITFIELD){
                throw new IOException("First received message should be bitfield");
            }

            new PeerMessage(MessageType.INTERESTED).send(socket.getOutputStream());

            Piece piece = null;
            int cutoff2 = 200;
            do {
                piece = new Piece(torrent, this.pieces.isEmpty() ? 0 : this.pieces.last().getId() + 1);
                logger.info("Downloading piece index {}", piece.getId());
                while (!piece.isComplete()) {
                    final var request = piece.nextRequest();
                    request.send(socket.getOutputStream());

                    PeerMessage resp;
                    int cutoff = 150;
                    do {
                        resp = PeerMessage.get(socket.getInputStream());
                        if (resp.getMessageType() != MessageType.PIECE) {
                            request.send(socket.getOutputStream());
                        }
                        if (cutoff-- < 0)
                            break;

                    } while (resp.getMessageType() != MessageType.PIECE);
                    piece.addPeerMessage(resp);
                }

                if (piece.verify()){
                    logger.info("Piece index {} downloaded successfully" , piece.getId());
                    this.pieces.add(piece);
                }
                if (cutoff2-- < 0)
                    break;
            } while (!this.isComplete());



        } catch (IOException ex) {
            logger.warn("Unable to establish connection {}", peer);
            throw ex;
        }

    }
    public boolean isComplete() {
        return this.pieces.size() == this.torrent.getPieceCount();
    }
}