package application.usecase;

import application.mapper.ApplicationLogMapper;
import domain.entity.ApplicationLog;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessApplicationLogUseCase {

    @Inject
    ApplicationLogMapper mapper;

    public Uni<Void> process(share.dto.ApplicationLog logDto) {
        ApplicationLog log = mapper.toEntity(logDto);
        return log.persist()
            .onItem().invoke(() -> Log.debugf("✅ Saved application log: %s - %s", 
                log.level, log.message))
            .onFailure().invoke(e -> Log.errorf(e, "❌ Failed to save application log"))
            .replaceWithVoid();
    }
}
