package com.github.gustavoflor.grpc.interceptor;

import io.grpc.*;

public class AuthInterceptor implements ServerInterceptor {

    private static final Metadata.Key<String> AUTHORIZATION_KEY = Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        final var authorization = metadata.get(AUTHORIZATION_KEY);
        if (authorization == null || !authorization.equals("Basic user:pass")) {
            final var status = Status.UNAUTHENTICATED.withDescription("Invalid token");
            serverCall.close(status, metadata);
        }
        return serverCallHandler.startCall(serverCall, metadata);
    }

}
