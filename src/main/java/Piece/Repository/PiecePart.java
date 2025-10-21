package Piece.Repository;

import lombok.NonNull;

record PiecePart(int begin, byte @NonNull [] piece) { }
