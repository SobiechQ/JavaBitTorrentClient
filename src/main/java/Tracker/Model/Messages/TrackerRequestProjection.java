package Tracker.Model.Messages;

import Tracker.Model.Tracker;
import lombok.Builder;

import java.net.URL;

@Builder
public record TrackerRequestProjection(Tracker tracker, URL url, String infoHashUrl, long uploaded, long downloaded, long left) {
}
