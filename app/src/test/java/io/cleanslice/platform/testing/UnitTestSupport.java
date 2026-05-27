package io.cleanslice.platform.testing;

import io.smallrye.mutiny.Uni;

public final class UnitTestSupport {

    private UnitTestSupport() {
    }

    public static <T> T await(Uni<T> uni) {
        return uni.await().indefinitely();
    }
}
