package com.github.gustavoflor.grpc.auth;

import io.grpc.CallCredentials;

import java.util.concurrent.Executor;

import static com.github.gustavoflor.grpc.util.MetadataUtil.getBasicAuthorization;

public class BasicCallCredentials extends CallCredentials {

    @Override
    public void applyRequestMetadata(RequestInfo requestInfo, Executor executor, MetadataApplier metadataApplier) {
        executor.execute(() -> {
            final var metadata = getBasicAuthorization("user:pass");
            metadataApplier.apply(metadata);
        });
    }

}
