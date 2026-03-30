package Configuration;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.awt.*;
import java.util.regex.Pattern;

@Plugin(
        name = "SwingAppender",
        category = "Core",
        elementType = "appender",
        printObject = true
)
public class SwingAppender extends AbstractAppender {
    private static final Pattern ANSI_PATTERN =
            Pattern.compile("\u001B\\[[;\\d]*m");


    protected SwingAppender(String name, Layout<?> layout) {
        super(name, null, layout, false, null);
    }

    @PluginFactory
    public static SwingAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<?> layout
    ) {
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new SwingAppender(name, layout);
    }

    @Override
    public void append(LogEvent event) {
        String msg = new String(getLayout().toByteArray(event));
        msg = ANSI_PATTERN.matcher(msg).replaceAll("").trim();

        Color color = mapLevelToColor(event.getLevel());

        LogWindow.append(msg + "\n", color);
    }

    private Color mapLevelToColor(Level level) {
        switch (level.name()) {
            case "ERROR":
                return Color.RED;
            case "WARN":
                return Color.ORANGE;
            case "INFO":
                return new Color(0, 128, 0); // ciemna zieleń
            case "DEBUG":
                return Color.BLUE;
            case "TRACE":
                return Color.GRAY;
            default:
                return Color.BLACK;
        }
    }
}