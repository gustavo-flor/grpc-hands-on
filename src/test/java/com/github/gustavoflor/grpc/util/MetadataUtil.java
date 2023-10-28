package com.github.gustavoflor.grpc.util;

import io.grpc.Metadata;

public class MetadataUtil {

    private static final Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);
    private static final Metadata METADATA = new Metadata();

    static {
        METADATA.put(AUTHORIZATION_KEY, "Basic user:pass");
    }

    private MetadataUtil() {
    }

    public static Metadata getMetadata() {
        return METADATA;
    }

}
