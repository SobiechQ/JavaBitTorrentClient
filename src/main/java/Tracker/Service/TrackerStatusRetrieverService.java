package Tracker.Service;

import Model.DecodedBencode.Torrent;

public interface TrackerStatusRetrieverService {
    long getUploaded(Torrent torrent);
    long getDownloaded(Torrent torrent);
    long getLeft(Torrent torrent);
}
