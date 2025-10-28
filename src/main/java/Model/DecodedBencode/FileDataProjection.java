package Model.DecodedBencode;

import java.util.List;

/**
 * @param path for example: ["data", "images", "cat.png"] => data/images/cat.png
 */
public record FileDataProjection(long length, List<String> path) { }
