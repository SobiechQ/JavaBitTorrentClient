package Piece.Repository;

import lombok.NonNull;

public record PiecePart(int begin, byte @NonNull [] piece) { }
