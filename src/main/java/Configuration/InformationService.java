package Configuration;

import Piece.Event.PieceCompletedEvent;
import lombok.NonNull;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

public interface InformationService {
    void logEvent(ApplicationEvent event);

    @Async
    @EventListener
    default void onApplicationEvent(@NonNull ApplicationEvent event){
        this.logEvent(event);
    }
}
