package Tracker.Model.Messages;

import lombok.Builder;

import java.net.URL;

@Builder
public record TrackerRequestProjection(URL url, String infoHashUrl, long uploaded, long downloaded, long left) {
}
