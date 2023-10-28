package com.github.gustavoflor.grpc.util;

import io.grpc.Metadata;

public class MetadataUtil {

    private static final Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    private MetadataUtil() {
    }

    public static Metadata getBasicAuthorization(final String credentials) {
        final var metadata = new Metadata();
        metadata.put(AUTHORIZATION_KEY, String.format("Basic %s", credentials));
        return metadata;
    }

}
