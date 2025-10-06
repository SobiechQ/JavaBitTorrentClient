package TCP;

import Model.DecodedBencode.Torrent;
import Utils.ByteUtils;
import com.google.common.hash.Hashing;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Stream;

import static TCP.MessageType.PIECE;
import static TCP.MessageType.REQUEST;

public class Piece {
    private record PieceMessage(@NonNull PeerMessage peerMessage, int id, int begin) {
        public PieceMessage(@NonNull PeerMessage peerMessage) {
            this(peerMessage, getId(peerMessage), getBegin(peerMessage));
        }

        private static int getId(@NonNull PeerMessage peerMessage) {
            if (peerMessage.getMessageType() != PIECE) {
                throw new IllegalArgumentException("Provided peer message does not represent piece");
            }
            final var arr = new byte[4];
            System.arraycopy(peerMessage.getData(), 0, arr, 0, 4);
            return ByteUtils.bytesToInt(arr);
        }

        private static int getBegin(@NonNull PeerMessage peerMessage) {
            if (peerMessage.getMessageType() != PIECE) {
                throw new IllegalArgumentException("Provided peer message does not represent piece");
            }
            final var arr = new byte[4];
            System.arraycopy(peerMessage.getData(), 4, arr, 0, 4);
            return ByteUtils.bytesToInt(arr);
        }

        private byte[] getData() {
            final var arr = new byte[peerMessage.getData().length - 8];
            System.arraycopy(peerMessage.getData(), 8, arr, 0, peerMessage.getData().length - 8);
            return arr;
        }
    }

    private final static int LENGTH = (int) Math.pow(2, 14);
    private final Torrent torrent;
    @Getter
    private final int id;
    private final SortedSet<PieceMessage> peerMessages;

    public Piece(@NonNull Torrent torrent, int id) {
        this.torrent = torrent;
        this.id = id;
        this.peerMessages = new TreeSet<>(Comparator.comparingInt(PieceMessage::begin));
    }

    public void addPeerMessage(@NonNull PeerMessage peerMessage) {
        final var pieceMessage = new PieceMessage(peerMessage);

        if (pieceMessage.id != this.id)
            throw new IllegalArgumentException("Provided peer message does not match piece ID");

        this.peerMessages.add(pieceMessage);
    }

    public boolean isComplete() {
        return this.lastBegin() == this.torrent.getPieceLength() - LENGTH;
    }


    public PeerMessage nextRequest() {
        if (this.isComplete())
            throw new IllegalStateException("Pieces are already complete");
        return new PeerMessage(REQUEST, this.id, peerMessages.isEmpty() ? 0 : lastBegin() + LENGTH, LENGTH);
    }

    private int lastBegin() {
        return Optional.ofNullable(peerMessages.isEmpty() ? null : peerMessages.last())
                .map(PieceMessage::begin)
                .orElse(0);
    }

    public Stream<PeerMessage> getMessages() {
        return this.peerMessages.stream()
                .map(PieceMessage::peerMessage);
    }

    public byte[] getBytes() {
        if (!this.isComplete())
            throw new IllegalStateException("Piece is not completed");

        return ByteUtils.unbox(
                this.peerMessages.stream()
                .map(PieceMessage::getData)
                .flatMap(ByteUtils::bytesToStream)
                .toArray(Byte[]::new)
        );
    }

    /**
     * Verifies checksum calculated on whole piece with hash found in torrent file.
     *
     * @return true if whole piece is complete and checksum matched. False otherwise.
     */

    @SuppressWarnings("UnstableApiUsage")
    public boolean verify() {
        if (!this.isComplete())
            return false;

        final var calculatedHashcode = Hashing.sha1().hashBytes(this.getBytes()).asBytes();
        final var actualHashcode = this.torrent
                .getPieceHash(this.id)
                .map(ByteUtils::unbox);

        return actualHashcode
                .map(ah -> Arrays.equals(ah, calculatedHashcode))
                .orElse(false);
    }


}
