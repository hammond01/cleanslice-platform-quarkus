package application.usecase;

import application.mapper.PerformanceLogMapper;
import domain.entity.PerformanceLog;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessPerformanceLogUseCase {

    @Inject
    PerformanceLogMapper mapper;

    public Uni<Void> process(share.dto.PerformanceLog logDto) {
        PerformanceLog log = mapper.toEntity(logDto);
        return log.persist()
            .onItem().invoke(() -> {
                String emoji = Boolean.TRUE.equals(log.isSlow) ? "🐌" : "⚡";
                Log.debugf("%s Saved performance log: %s - %dms", 
                    emoji, log.operation, log.durationMs);
            })
            .onFailure().invoke(e -> Log.errorf(e, "❌ Failed to save performance log"))
            .replaceWithVoid();
    }
}
