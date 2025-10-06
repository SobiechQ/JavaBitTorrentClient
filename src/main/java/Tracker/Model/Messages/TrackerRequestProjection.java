package Tracker.Model.Messages;

import java.net.URL;

public record TrackerRequestProjection(URL url, String infoHashUrl, long uploaded, long downloaded, long left) {
}
