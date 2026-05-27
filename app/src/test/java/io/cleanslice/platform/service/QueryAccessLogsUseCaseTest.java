package io.cleanslice.platform.service;

import io.cleanslice.platform.application.port.out.persistence.AccessLogRepository;
import io.cleanslice.platform.domain.AccessLog;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.cleanslice.platform.testing.UnitTestSupport.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QueryAccessLogsUseCaseTest {

    @Mock
    AccessLogRepository accessLogRepository;

    @InjectMocks
    QueryAccessLogsUseCase queryAccessLogsUseCase;

    @Test
    void getAllLogs_shouldDelegateToRepository() {
        AccessLog accessLog = new AccessLog();
        accessLog.endpoint = "/api/v1/products";
        List<AccessLog> expected = List.of(accessLog);
        when(accessLogRepository.getAllLogs(0, 20)).thenReturn(Uni.createFrom().item(expected));

        List<AccessLog> result = await(queryAccessLogsUseCase.getAllLogs(0, 20));

        assertEquals(1, result.size());
        assertEquals("/api/v1/products", result.getFirst().endpoint);
        verify(accessLogRepository).getAllLogs(0, 20);
    }

    @Test
    void countByMethod_shouldDelegateToRepository() {
        when(accessLogRepository.countByMethod("GET")).thenReturn(Uni.createFrom().item(12L));

        Long result = await(queryAccessLogsUseCase.countByMethod("GET"));

        assertEquals(12L, result);
        verify(accessLogRepository).countByMethod("GET");
    }
}
