package application.usecase;

import application.mapper.AccessLogMapper;
import domain.entity.AccessLog;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessAccessLogUseCase {

    @Inject
    AccessLogMapper mapper;

    public Uni<Void> process(share.dto.AccessLog logDto) {
        AccessLog log = mapper.toEntity(logDto);
        return log.persist()
            .onItem().invoke(() -> Log.debugf("🌐 Saved access log: %s %s - %d", 
                log.httpMethod, log.endpoint, log.statusCode))
            .onFailure().invoke(e -> Log.errorf(e, "❌ Failed to save access log"))
            .replaceWithVoid();
    }
}
