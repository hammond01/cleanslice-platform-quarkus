package application.usecase;

import application.mapper.ErrorLogMapper;
import domain.entity.ErrorLog;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ProcessErrorLogUseCase {

    @Inject
    ErrorLogMapper mapper;

    @WithTransaction
    public Uni<Void> process(share.dto.ErrorLog logDto) {
        ErrorLog log = mapper.toEntity(logDto);
        return log.persist()
            .onItem().invoke(() -> Log.warnf("⚠️ Saved error log: %s - %s", 
                log.exceptionType, log.message))
            .onFailure().invoke(e -> Log.errorf(e, "❌ Failed to save error log"))
            .replaceWithVoid();
    }
}
